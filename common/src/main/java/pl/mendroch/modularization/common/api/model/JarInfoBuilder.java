package pl.mendroch.modularization.common.api.model;

import java.util.jar.Manifest;

public class JarInfoBuilder {
    private Manifest manifest;

    public JarInfoBuilder setManifest(Manifest manifest) {
        this.manifest = manifest;
        return this;
    }

    public JarInfo createJarInfo() {
        return new JarInfo(manifest);
    }
}