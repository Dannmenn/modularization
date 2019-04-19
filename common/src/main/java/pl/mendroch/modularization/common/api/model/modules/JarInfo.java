package pl.mendroch.modularization.common.api.model.modules;

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
    public String toString() {
        return name + ":" + specificationVersion;
    }
}
