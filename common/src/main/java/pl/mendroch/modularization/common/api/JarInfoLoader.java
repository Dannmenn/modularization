package pl.mendroch.modularization.common.api;

import pl.mendroch.modularization.common.api.model.JarInfo;
import pl.mendroch.modularization.common.api.model.JarInfoBuilder;
import pl.mendroch.modularization.common.api.model.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.ModuleJarInfoBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public final class JarInfoLoader {
    private JarInfoLoader() {
        //Hide implicit constructor
    }

    public static JarInfo loadJarInformation(Path path) throws IOException {
        JarInfoBuilder builder = new JarInfoBuilder();
        try (JarFile jarFile = new JarFile(path.toFile())) {
            builder.setManifest(jarFile.getManifest()).setVersion(jarFile.getVersion());
        }
        return builder.createJarInfo();
    }

    public static ModuleJarInfo loadModuleInformation(Path path) throws IOException {
        ModuleJarInfoBuilder builder = new ModuleJarInfoBuilder();
        ModuleFinder finder = ModuleFinder.of(path);
        for (ModuleReference reference : finder.findAll()) {
            builder.setDescriptor(reference.descriptor());
        }
        try (JarFile jarFile = new JarFile(path.toFile())) {
            builder.setManifest(jarFile.getManifest()).setVersion(jarFile.getVersion());
            ZipEntry entry = jarFile.getEntry(Paths.get("META-INF", "dependencies.properties").toString());
            if (entry != null) {
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                    Properties dependencies = new Properties();
                    dependencies.load(inputStream);
                    builder.setDependencies(dependencies);
                }
            }
        }
        return builder.createModuleJarInfo();
    }
}
