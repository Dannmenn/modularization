package pl.mendroch.modularization.common.api.model;

import java.lang.module.ModuleDescriptor;
import java.util.Properties;
import java.util.jar.Manifest;

public class ModuleJarInfoBuilder {
    private Manifest manifest;
    private Runtime.Version version;
    private Properties dependencies;
    private ModuleDescriptor descriptor;

    public ModuleJarInfoBuilder setManifest(Manifest manifest) {
        this.manifest = manifest;
        return this;
    }

    public ModuleJarInfoBuilder setVersion(Runtime.Version version) {
        this.version = version;
        return this;
    }

    public ModuleJarInfoBuilder setDependencies(Properties dependencies) {
        this.dependencies = dependencies;
        return this;
    }

    public ModuleJarInfoBuilder setDescriptor(ModuleDescriptor descriptor) {
        this.descriptor = descriptor;
        return this;
    }

    public ModuleJarInfo createModuleJarInfo() {
        return new ModuleJarInfo(manifest, version, dependencies, descriptor);
    }
}