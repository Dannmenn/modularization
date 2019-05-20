package pl.mendroch.modularization.common.api.model.modules;

import pl.mendroch.modularization.common.api.annotation.InternalTesting;

import java.nio.file.Path;

public class JarInfoBuilder {
    private String name;
    private String fileName;
    private String specificationTitle;
    private String specificationVersion;
    private String implementationVersion;
    private String mainClass;
    private Path path;

    private JarInfoBuilder() {
    }

    public static JarInfoBuilder jarInfoBuilder() {
        return new JarInfoBuilder();
    }

    public JarInfoBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public JarInfoBuilder setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public JarInfoBuilder setSpecificationTitle(String specificationTitle) {
        this.specificationTitle = specificationTitle;
        return this;
    }

    public JarInfoBuilder setSpecificationVersion(String specificationVersion) {
        this.specificationVersion = specificationVersion;
        return this;
    }

    public JarInfoBuilder setImplementationVersion(String implementationVersion) {
        this.implementationVersion = implementationVersion;
        return this;
    }

    public JarInfoBuilder setMainClass(String mainClass) {
        this.mainClass = mainClass;
        return this;
    }

    public JarInfoBuilder setPath(Path path) {
        this.path = path;
        return this;
    }

    public JarInfo createJarInfo() {
        return new JarInfo(name, path, fileName, specificationTitle, specificationVersion, implementationVersion, mainClass);
    }
}