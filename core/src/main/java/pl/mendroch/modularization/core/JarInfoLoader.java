package pl.mendroch.modularization.core;

import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfoBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import static java.util.jar.Attributes.Name.*;
import static pl.mendroch.modularization.common.api.model.modules.JarInfoBuilder.jarInfoBuilder;

public final class JarInfoLoader {
    private JarInfoLoader() {
        //Hide implicit constructor
    }

    public static JarInfo loadJarInformation(Path path, ModuleDescriptor descriptor) {
        return readJarFile(path, descriptor, null);
    }

    private static JarInfo readJarFile(Path path, ModuleDescriptor descriptor, Consumer<JarFile> jarFileConsumer) {
        try (JarFile jarFile = new JarFile(path.toFile())) {
            if (jarFileConsumer != null) {
                jarFileConsumer.accept(jarFile);
            }
            Manifest manifest = jarFile.getManifest();
            Attributes mainAttributes = manifest.getMainAttributes();
            return jarInfoBuilder()
                    .setPath(path)
                    .setFileName(path.getFileName().toString())
                    .setName(mainAttributes.getValue("Name"))
                    .setMainClass(mainAttributes.getValue(MAIN_CLASS))
                    .setSpecificationTitle(mainAttributes.getValue(SPECIFICATION_TITLE))
                    .setSpecificationVersion(mainAttributes.getValue(SPECIFICATION_VERSION))
                    .setImplementationVersion(mainAttributes.getValue(IMPLEMENTATION_VERSION))
                    .setMissing(descriptor)
                    .createJarInfo();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
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
        Set<ModuleReference> references = finder.findAll();
        assert references.size() == 1 : "Found more than one module: " + path;
        for (ModuleReference reference : references) {
            builder.setDescriptor(reference.descriptor());
        }
        builder.setJarInfo(
                readJarFile(path, builder.getDescriptor(), jarFile -> {
                    try {
                        ZipEntry entry = getDependenciesEntry(jarFile);
                        if (entry != null) {
                            try (InputStream inputStream = jarFile.getInputStream(entry)) {
                                Properties dependencies = new Properties();
                                dependencies.load(inputStream);
                                builder.setDependencyVersions(dependencies);
                            }
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }));
        return builder.createModuleJarInfo();
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
