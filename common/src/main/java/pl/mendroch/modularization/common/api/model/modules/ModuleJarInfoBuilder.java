package pl.mendroch.modularization.common.api.model.modules;

import lombok.Getter;

import java.lang.module.ModuleDescriptor;
import java.util.Properties;

@SuppressWarnings("UnusedReturnValue")
public class ModuleJarInfoBuilder {
    private JarInfo jarInfo;
    @Getter
    private ModuleDescriptor descriptor;
    private Properties dependencyVersions = new Properties();

    public ModuleJarInfoBuilder setJarInfo(JarInfo jarInfo) {
        this.jarInfo = jarInfo;
        return this;
    }

    public ModuleJarInfoBuilder setDescriptor(ModuleDescriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public void setDependencyVersions(Properties dependencyVersions) {
        this.dependencyVersions = dependencyVersions;
    }

    public ModuleJarInfo createModuleJarInfo() {
        return new ModuleJarInfo(jarInfo, dependencyVersions, descriptor);
    }
}