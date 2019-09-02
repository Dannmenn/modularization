package pl.mendroch.modularization.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public final class JarUtils {
    public JarUtils() {
        //Hide implicit constructor
    }

    public static Path createZipJar(Path root, Path manifest, Path... files) throws IOException {
        Path output = Files.createTempFile("zip.", ".jar");
        try (JarOutputStream zs = new JarOutputStream(Files.newOutputStream(output), createManifest(manifest))) {
            for (Path file : files) {
                ZipEntry zipEntry = new ZipEntry(root.relativize(file).toString());
                try {
                    zs.putNextEntry(zipEntry);
                    Files.copy(file, zs);
                    zs.closeEntry();
                } catch (IOException e) {
                    //noinspection ThrowablePrintedToSystemOut
                    System.err.println(e);
                }
            }
        }
        return output;
    }

    private static Manifest createManifest(Path manifest) throws IOException {
        try (InputStream is = Files.newInputStream(manifest)) {
            return new Manifest(is);
        }
    }

    public static Path createDirectoryJar(Path manifest, Path directory) throws IOException {
        Path output = Files.createTempFile("zip.", ".jar");
        try (JarOutputStream zs = new JarOutputStream(Files.newOutputStream(output), createManifest(manifest))) {
            Files.walk(directory)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(directory.relativize(path).toString());
                        try {
                            zs.putNextEntry(zipEntry);
                            Files.copy(path, zs);
                            zs.closeEntry();
                        } catch (IOException e) {
                            //noinspection ThrowablePrintedToSystemOut
                            System.err.println(e);
                        }
                    });
        }
        return output;
    }
}
