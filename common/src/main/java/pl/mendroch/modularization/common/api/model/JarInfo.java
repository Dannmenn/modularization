package pl.mendroch.modularization.common.api.model;

import java.lang.Runtime.Version;
import java.util.jar.Manifest;

public class JarInfo {
    final Manifest manifest;
    final Version version;

    JarInfo(Manifest manifest, Version version) {
        this.manifest = manifest;
        this.version = version;
    }

    public Manifest getManifest() {
        return manifest;
    }

    public Version getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "JarInfo{" +
                "manifest=" + manifest.getMainAttributes().entrySet() +
                ", version=" + version +
                '}';
    }
}
