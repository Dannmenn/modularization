package pl.mendroch.modularization.common.api;

import pl.mendroch.modularization.common.api.model.JarInfo;
import pl.mendroch.modularization.common.api.model.JarInfoBuilder;
import pl.mendroch.modularization.common.api.model.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.ModuleJarInfoBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public final class JarInfoLoader {
    private JarInfoLoader() {
        //Hide implicit constructor
    }

    public static JarInfo loadJarInformation(Path path) {
        JarInfoBuilder builder = new JarInfoBuilder();
        try (JarFile jarFile = new JarFile(path.toFile())) {
            builder.setManifest(jarFile.getManifest());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.createJarInfo();
    }

    public static List<ModuleJarInfo> loadModulesInformation(Path path) throws IOException {
        return Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .map(JarInfoLoader::loadModuleInformation)
                .collect(Collectors.toList());
    }

    public static ModuleJarInfo loadModuleInformation(Path path) {
        ModuleJarInfoBuilder builder = new ModuleJarInfoBuilder();
        ModuleFinder finder = ModuleFinder.of(path);
        //assert only one
        for (ModuleReference reference : finder.findAll()) {
            builder.setDescriptor(reference.descriptor());
        }
        try (JarFile jarFile = new JarFile(path.toFile())) {
            builder.setManifest(jarFile.getManifest());
            ZipEntry entry = getDependenciesEntry(jarFile);
            if (entry != null) {
                try (InputStream inputStream = jarFile.getInputStream(entry)) {
                    Properties dependencies = new Properties();
                    dependencies.load(inputStream);
                    builder.setDependencies(dependencies);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.createModuleJarInfo();
    }

    private static String extractVersion(Path fileName) {
        String file = fileName.toString();
        return file.substring(file.indexOf("-"), file.lastIndexOf("."));
    }

    private static ZipEntry getDependenciesEntry(JarFile jarFile) {
        ZipEntry entry = jarFile.getEntry("META-INF/dependencies.properties");
        if (entry == null) {
            System.err.println("Fallback to windows path: should not happened");
            return jarFile.getEntry("META-INF\\dependencies.properties");
        }
        return entry;
    }
}
