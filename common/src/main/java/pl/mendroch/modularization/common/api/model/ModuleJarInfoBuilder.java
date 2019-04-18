package pl.mendroch.modularization.common.api.model;

import java.lang.module.ModuleDescriptor;
import java.util.HashSet;
import java.util.Set;

public class ModuleJarInfoBuilder {
    private JarInfo jarInfo;
    private Set<Dependency> dependencies = new HashSet<>();
    private ModuleDescriptor descriptor;

    public ModuleJarInfoBuilder setJarInfo(JarInfo jarInfo) {
        this.jarInfo = jarInfo;
        return this;
    }

    public ModuleJarInfoBuilder setDependencies(Set<Dependency> dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public ModuleJarInfoBuilder setDescriptor(ModuleDescriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public ModuleJarInfo createModuleJarInfo() {
        return new ModuleJarInfo(jarInfo, dependencies, descriptor);
    }
}