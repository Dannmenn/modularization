package pl.mendroch.modularization.common.api.model;

import java.lang.module.ModuleDescriptor;
import java.util.Objects;
import java.util.Set;

public class ModuleJarInfo {
    private final JarInfo jarInfo;
    private final Set<Dependency> dependencies;
    private final ModuleDescriptor descriptor;

    ModuleJarInfo(JarInfo jarInfo, Set<Dependency> dependencies, ModuleDescriptor descriptor) {
        this.jarInfo = jarInfo;
        this.dependencies = dependencies;
        this.descriptor = descriptor;
    }

    public JarInfo getJarInfo() {
        return jarInfo;
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    public ModuleDescriptor getDescriptor() {
        return descriptor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleJarInfo that = (ModuleJarInfo) o;
        return jarInfo.equals(that.jarInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jarInfo);
    }

    @Override
    public String toString() {
        return jarInfo + ":" + descriptor.toNameAndVersion();
    }
}
