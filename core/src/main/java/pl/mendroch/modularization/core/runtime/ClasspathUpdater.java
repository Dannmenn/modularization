package pl.mendroch.modularization.core.runtime;

import jdk.internal.loader.Loader;
import jdk.internal.misc.Unsafe;
import lombok.extern.java.Log;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.core.model.LoadedModuleReference;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.lang.module.ResolvedModule;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.module.Configuration.resolveAndBind;
import static java.util.Collections.emptyList;
import static java.util.logging.Level.SEVERE;
import static java.util.stream.Collectors.toSet;

@Log
class ClasspathUpdater {
    private final List<ModuleJarInfo> modules;

    ClasspathUpdater(List<ModuleJarInfo> modules) {
        this.modules = modules;
    }

    LoadedModuleReference[] updateClassLoaders(LoadedModuleReference[] references, LoadedModuleReference parentReference) {
        LoadedModuleReference[] result = new LoadedModuleReference[modules.size()];
        LoadedModuleReference parent = parentReference;
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
        updateParentFieldWithUnsafe(parent.getLoader(), ClassLoader.class, loader);
        updateParentFieldWithUnsafe(parent.getLoader(), Loader.class, loader);
        Configuration configuration = current.getConfiguration();
        updateFieldWithReflection(emptyList(), "parents", Configuration.class, configuration);
        Configuration conf = resolveAndBind(
                new ModuleFinderDelegate(configuration),
                List.of(parent.getConfiguration()),
                ModuleFinder.of(),
                List.of(current.getModuleName())
        );
        ModuleLayer layer = current.getLayer();
        updateFieldWithReflection(conf, "cf", ModuleLayer.class, layer);
        updateFieldWithReflection(List.of(parent.getLayer()), "parents", ModuleLayer.class, layer);
        updateFieldWithReflection(new ConcurrentHashMap<String, ClassLoader>(), "remotePackageToLoader", Loader.class, loader);
        ((Loader) loader).initRemotePackageMap(conf, List.of(parent.getLayer()));
        return new LoadedModuleReference(
                current.getModuleName(), current.getModule(), conf, layer, loader
        );
    }

    private int findModule(LoadedModuleReference[] references, int referenceIndex, ModuleJarInfo module) {
        int tmpIndex = referenceIndex;
        while (tmpIndex >= 0) {
            if (module.equals(references[tmpIndex].getModule())) {
                break;
            }
            tmpIndex--;
        }
        return tmpIndex;
    }

    private void updateParentFieldWithUnsafe(Object value, Class<?> aClass, ClassLoader object) {
        Unsafe unsafe = Unsafe.getUnsafe();
        long offset = unsafe.objectFieldOffset(aClass, "parent");
        unsafe.putReference(object, offset, value);
    }

    private void updateFieldWithReflection(Object value, String name, Class<?> aClass, Object object) {
        Field field = null;
        try {
            field = aClass.getDeclaredField(name);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception e) {
            log.log(SEVERE, e.getMessage(), e);
        } finally {
            if (field != null) field.setAccessible(false);
        }
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
