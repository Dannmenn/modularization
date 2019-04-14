package pl.mendroch.modularization.common.api.model;

import java.lang.Runtime.Version;
import java.util.jar.Manifest;

public class JarInfoBuilder {
    private Manifest manifest;
    private Version version;

    public JarInfoBuilder setManifest(Manifest manifest) {
        this.manifest = manifest;
        return this;
    }

    public JarInfoBuilder setVersion(Version version) {
        this.version = version;
        return this;
    }

    public JarInfo createJarInfo() {
        return new JarInfo(manifest, version);
    }
}