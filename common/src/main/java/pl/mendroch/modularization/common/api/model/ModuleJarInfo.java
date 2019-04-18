package pl.mendroch.modularization.common.api.model;

import java.lang.module.ModuleDescriptor;
import java.util.Properties;
import java.util.jar.Manifest;

public class ModuleJarInfo extends JarInfo {
    private final Properties dependencies;
    private final ModuleDescriptor descriptor;

    ModuleJarInfo(Manifest manifest, Properties dependencies, ModuleDescriptor descriptor) {
        super(manifest);
        this.dependencies = dependencies;
        this.descriptor = descriptor;
    }

    public Properties getDependencies() {
        return dependencies;
    }

    public ModuleDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public String toString() {
        return "ModuleJarInfo{" +
                "dependencies=" + dependencies +
                ", descriptor=" + descriptor.toNameAndVersion() +
                ", manifest=" + manifest.getMainAttributes().entrySet() +
                '}';
    }
}
