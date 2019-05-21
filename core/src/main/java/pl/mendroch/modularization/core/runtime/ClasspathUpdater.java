package pl.mendroch.modularization.core.runtime;

import jdk.internal.loader.Loader;
import jdk.internal.misc.Unsafe;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.core.model.LoadedModuleReference;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.ModuleLayer.defineModules;
import static java.lang.module.Configuration.resolveAndBind;
import static java.util.stream.Collectors.toSet;

class ClasspathUpdater {
    private final List<ModuleJarInfo> modules;

    ClasspathUpdater(List<ModuleJarInfo> modules) {
        this.modules = modules;
    }

    LoadedModuleReference[] updateClassLoaders(LoadedModuleReference[] references) {
        LoadedModuleReference[] result = new LoadedModuleReference[modules.size()];
        LoadedModuleReference parent = new LoadedModuleReferenceMock(ModuleLayer.boot(), getClass().getClassLoader());
        for (int i = modules.size() - 1, referenceIndex = references.length - 1; i >= 0; i--) {
            ModuleJarInfo module = modules.get(i);
            int tmpIndex = findModule(references, referenceIndex, module);
            if (tmpIndex >= 0) {
                referenceIndex = tmpIndex - 1;
                result[i] = useExistingClassLoader(references[tmpIndex], parent);
            } else {
                result[i] = createNewModuleReference(parent, module);
            }
            parent = result[i];
        }
        return result;
    }

    private LoadedModuleReference createNewModuleReference(LoadedModuleReference parent, ModuleJarInfo module) {
        String moduleName = module.getDescriptor().name();
        JarInfo jarInfo = module.getJarInfo();
        Configuration conf = resolveAndBind(
                ModuleFinder.of(jarInfo.getPath()),
                List.of(parent.getConfiguration()),
                ModuleFinder.of(),
                List.of(moduleName)
        );
        ModuleLayer layer = parent.getLayer().defineModulesWithOneLoader(conf, parent.getLoader());
        ClassLoader loader = layer.findLoader(moduleName);
        return new LoadedModuleReference(moduleName, module, conf, layer, loader);
    }

    private LoadedModuleReference useExistingClassLoader(LoadedModuleReference reference, LoadedModuleReference parent) {
        ClassLoader loader = reference.getLoader();
        if (loader.getParent().equals(parent.getLoader())) {
            return reference;
        }
        return recreateLoadedModuleReference(parent, reference);
    }

    private LoadedModuleReference recreateLoadedModuleReference(LoadedModuleReference parent, LoadedModuleReference current) {
        ClassLoader loader = current.getLoader();
        updateField(parent.getLoader(), "parent", ClassLoader.class, loader);
        updateField(parent.getLoader(), "parent", Loader.class, loader);
        Configuration conf = resolveAndBind(
                new ModuleFinderDelegate(parent.getConfiguration()),
                List.of(parent.getConfiguration()),
                ModuleFinder.of(),
                List.of(current.getModuleName())
        );
        final ClassLoader tmpLoader = loader;
        ModuleLayer layer = defineModules(conf, List.of(parent.getLayer()), mn -> tmpLoader).layer();
        updateField(new ConcurrentHashMap<String, ClassLoader>(), "remotePackageToLoader", Loader.class, loader);
        ((Loader) loader).initRemotePackageMap(parent.getConfiguration(), List.of(parent.getLayer()));
        return new LoadedModuleReference(
                current.getModuleName(), current.getModule(), conf, layer, loader
        );
    }

    private int findModule(LoadedModuleReference[] references, int referenceIndex, ModuleJarInfo module) {
        int tmpIndex = referenceIndex;
        while (tmpIndex >= 0) {
            if (module.equals(references[tmpIndex--].getModule())) {
                break;
            }
        }
        return tmpIndex;
    }

    private void updateField(Object value, String name, Class<?> aClass, Object object) {
        Unsafe unsafe = Unsafe.getUnsafe();
        long offset;
        offset = unsafe.objectFieldOffset(aClass, name);
        unsafe.compareAndSetReference(object, offset, null, value);
    }

    private static class LoadedModuleReferenceMock extends LoadedModuleReference {
        private LoadedModuleReferenceMock(ModuleLayer layer, ClassLoader loader) {
            super(null, null, layer.configuration(), layer, loader);
        }
    }

    private static class ModuleFinderDelegate implements ModuleFinder {
        private final Configuration configuration;

        private ModuleFinderDelegate(Configuration configuration) {
            this.configuration = configuration;
        }

        @Override
        public Optional<ModuleReference> find(String name) {
            return configuration.findModule(name).map(ResolvedModule::reference);
        }

        @Override
        public Set<ModuleReference> findAll() {
            return configuration.modules().stream().map(ResolvedModule::reference).collect(toSet());
        }
    }
}
