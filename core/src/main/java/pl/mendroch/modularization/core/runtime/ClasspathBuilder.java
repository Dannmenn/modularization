package pl.mendroch.modularization.core.runtime;

import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.core.model.LoadedModuleReference;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.util.List;

class ClasspathBuilder {
    private final List<ModuleJarInfo> modules;

    ClasspathBuilder(List<ModuleJarInfo> modules) {
        this.modules = modules;
    }

    LoadedModuleReference[] buildClassLoaders() {
        LoadedModuleReference[] result = new LoadedModuleReference[modules.size()];
        Configuration conf = ModuleLayer.boot().configuration();
        ModuleLayer layer = ModuleLayer.boot();
        ClassLoader loader = getClass().getClassLoader();
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
}
