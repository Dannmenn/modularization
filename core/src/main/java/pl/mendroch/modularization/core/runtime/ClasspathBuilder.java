package pl.mendroch.modularization.core.runtime;

import pl.mendroch.modularization.common.api.loader.ThirdPartyModuleConfigurator;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.core.model.LoadedModuleReference;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Path;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Collectors;

class ClasspathBuilder {
    private final List<ModuleJarInfo> modules;

    ClasspathBuilder(List<ModuleJarInfo> modules) {
        this.modules = modules;
    }

    LoadedModuleReference[] buildClassLoaders(Set<ModuleJarInfo> thirdPartyJars) {
        LoadedModuleReference[] result = new LoadedModuleReference[modules.size()];
        Configuration conf = ModuleLayer.boot().configuration();
        ModuleLayer layer = ModuleLayer.boot();
        ClassLoader loader = getClass().getClassLoader();
        if (!thirdPartyJars.isEmpty()) {
            List<String> moduleNames = thirdPartyJars.stream()
                    .map(info -> info.getDescriptor().name())
                    .collect(Collectors.toList());
            conf = Configuration.resolveAndBind(
                    ModuleFinder.of(
                            thirdPartyJars.stream()
                                    .map(info -> info.getJarInfo().getPath())
                                    .toArray(Path[]::new)
                    ),
                    List.of(conf),
                    ModuleFinder.of(),
                    moduleNames
            );
            layer = layer.defineModulesWithOneLoader(conf, loader);
            configureThirdPartyModules(layer);
            loader = layer.findLoader(moduleNames.get(0));
        }
        for (int i = modules.size() - 1; i >= 0; i--) {
            ModuleJarInfo module = modules.get(i);
            JarInfo jarInfo = module.getJarInfo();
            String moduleName = module.getDescriptor().name();
            conf = Configuration.resolveAndBind(
                    ModuleFinder.of(jarInfo.getPath()),
                    List.of(conf),
                    ModuleFinder.of(),
                    List.of(moduleName)
            );
            layer = layer.defineModulesWithOneLoader(conf, loader);
            loader = layer.findLoader(moduleName);
            result[i] = new LoadedModuleReference(moduleName, module, conf, layer, loader);
        }
        return result;
    }

    private void configureThirdPartyModules(ModuleLayer layer) {
        ServiceLoader<ThirdPartyModuleConfigurator> configurators = ServiceLoader.load(ThirdPartyModuleConfigurator.class);
        for (ThirdPartyModuleConfigurator configurator : configurators) {
            configurator.configure(layer);
        }
    }
}
