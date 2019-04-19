package pl.mendroch.modularization.common.api.model.modules;

public class JarInfo {
    final String fileName;
    final String SpecificationTitle;
    final String SpecificationVersion;
    final String ImplementationVersion;
    final String MainClass;

    JarInfo(String fileName, String specificationTitle, String specificationVersion, String implementationVersion, String mainClass) {
        this.fileName = fileName;
        SpecificationTitle = specificationTitle;
        SpecificationVersion = specificationVersion;
        ImplementationVersion = implementationVersion;
        MainClass = mainClass;
    }

    public String getFileName() {
        return fileName;
    }

    public String getSpecificationTitle() {
        return SpecificationTitle;
    }

    public String getSpecificationVersion() {
        return SpecificationVersion;
    }

    public String getImplementationVersion() {
        return ImplementationVersion;
    }

    public String getMainClass() {
        return MainClass;
    }

    @Override
    public String toString() {
        return fileName + ":" + SpecificationVersion;
    }
}
