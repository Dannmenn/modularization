package pl.mendroch.modularization.core.runtime;

import lombok.Getter;
import pl.mendroch.modularization.common.api.loader.ModuleConfigurator;
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
    @Getter
    private LoadedModuleReference parent;

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
            loader = layer.findLoader(moduleNames.get(0));
        }
        parent = new LoadedModuleReference(null, null, conf, layer, loader);
        for (int i = 0; i < modules.size(); i++) {
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
        configureModules(layer);
        return result;
    }

    private void configureModules(ModuleLayer layer) {
        ServiceLoader<ModuleConfigurator> configurators = ServiceLoader.load(ModuleConfigurator.class);
        for (ModuleConfigurator configurator : configurators) {
            configurator.configure(layer);
        }
    }
}
