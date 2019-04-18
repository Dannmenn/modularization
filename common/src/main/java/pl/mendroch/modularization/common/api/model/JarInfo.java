package pl.mendroch.modularization.common.api.model;

import java.util.jar.Manifest;

public class JarInfo {
    final Manifest manifest;

    JarInfo(Manifest manifest) {
        this.manifest = manifest;
    }

    public Manifest getManifest() {
        return manifest;
    }

    @Override
    public String toString() {
        return "JarInfo{" +
                "manifest=" + manifest.getMainAttributes().entrySet() +
                '}';
    }
}
