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
        Configuration conf = ModuleLayer.boot().configuration();
        ModuleLayer layer = ModuleLayer.boot();
        ClassLoader loader = getClass().getClassLoader();
        int referenceIndex = references.length - 1;
        for (int i = modules.size() - 1; i >= 0; i--) {
            ModuleJarInfo module = modules.get(i);
            int tmp = referenceIndex;
            while (tmp >= 0) {
                if (module.equals(references[tmp--].getModule())) {
                    break;
                }
            }
            if (tmp >= 0) {
                referenceIndex = tmp - 1;
                LoadedModuleReference reference = references[tmp];
                result[i] = reference;
                Configuration prevConf = conf;
                ModuleLayer prevLayer = layer;
                ClassLoader prevLoader = loader;
                loader = reference.getLoader();
                if (!loader.getParent().equals(prevLoader)) {
                    updateField(prevLoader, "parent", ClassLoader.class, loader);
                    updateField(prevLoader, "parent", Loader.class, loader);
                    conf = resolveAndBind(
                            new ModuleFinderDelegate(prevConf),
                            List.of(prevConf),
                            ModuleFinder.of(),
                            List.of(reference.getModuleName())
                    );
                    final ClassLoader tmpLoader = loader;
                    layer = defineModules(conf, List.of(prevLayer), mn -> tmpLoader).layer();
                    updateField(new ConcurrentHashMap<String, ClassLoader>(), "remotePackageToLoader", Loader.class, loader);
                    ((Loader) loader).initRemotePackageMap(prevConf, List.of(prevLayer));
                    result[i] = new LoadedModuleReference(
                            reference.getModuleName(), reference.getModule(), conf, layer, loader
                    );
                } else {
                    result[i] = reference;
                    conf = reference.getConfiguration();
                    layer = reference.getLayer();
                    loader = reference.getLoader();
                }
            } else {
                String moduleName = module.getDescriptor().name();
                JarInfo jarInfo = module.getJarInfo();
                conf = resolveAndBind(
                        ModuleFinder.of(jarInfo.getPath()),
                        List.of(conf),
                        ModuleFinder.of(),
                        List.of(moduleName)
                );
                layer = layer.defineModulesWithOneLoader(conf, loader);
                loader = layer.findLoader(moduleName);
                result[i] = new LoadedModuleReference(moduleName, module, conf, layer, loader);
            }
        }
        return result;
    }

    private void updateField(Object value, String name, Class<?> aClass, Object object) {
        Unsafe unsafe = Unsafe.getUnsafe();
        long offset;
        offset = unsafe.objectFieldOffset(aClass, name);
        unsafe.compareAndSetReference(object, offset, null, value);
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
