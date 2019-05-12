package pl.mendroch.modularization.common.api.model.modules;

import java.util.Objects;

public class JarInfo {
    private final String name;
    private final String fileName;
    private final String specificationTitle;
    private final String specificationVersion;
    private final String implementationVersion;
    private final String mainClass;

    JarInfo(String name, String fileName, String specificationTitle, String specificationVersion, String implementationVersion, String mainClass) {
        this.name = name;
        this.fileName = fileName;
        this.specificationTitle = specificationTitle;
        this.specificationVersion = specificationVersion;
        this.implementationVersion = implementationVersion;
        this.mainClass = mainClass;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSpecificationTitle() {
        return specificationTitle;
    }

    public String getSpecificationVersion() {
        return specificationVersion;
    }

    public String getImplementationVersion() {
        return implementationVersion;
    }

    public String getMainClass() {
        return mainClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JarInfo jarInfo = (JarInfo) o;
        return name.equals(jarInfo.name) &&
                specificationVersion.equals(jarInfo.specificationVersion) &&
                implementationVersion.equals(jarInfo.implementationVersion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, specificationVersion, implementationVersion);
    }

    @Override
    public String toString() {
        return name + ":" + specificationVersion;
    }
}
