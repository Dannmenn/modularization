package pl.mendroch.modularization.core.model;

import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.lang.module.Configuration;

public class LoadedModuleReference {
    private final String moduleName;
    private final ModuleJarInfo module;
    private final Configuration configuration;
    private final ModuleLayer layer;
    private final ClassLoader loader;

    public LoadedModuleReference(String moduleName, ModuleJarInfo module, Configuration configuration, ModuleLayer layer, ClassLoader loader) {
        this.moduleName = moduleName;
        this.module = module;
        this.configuration = configuration;
        this.layer = layer;
        this.loader = loader;
    }

    public String getModuleName() {
        return moduleName;
    }

    public ModuleJarInfo getModule() {
        return module;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public ModuleLayer getLayer() {
        return layer;
    }

    public ClassLoader getLoader() {
        return loader;
    }
}
