package pl.mendroch.modularization.common.api;

import org.junit.Before;
import org.junit.Test;
import pl.mendroch.modularization.common.api.model.JarInfo;
import pl.mendroch.modularization.common.api.model.ModuleJarInfo;
import pl.mendroch.modularization.common.utils.JarUtils;

import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.jar.Manifest;

import static java.util.jar.Attributes.Name.SPECIFICATION_VERSION;
import static java.util.jar.JarFile.MANIFEST_NAME;
import static org.junit.Assert.*;

public class SingleJarInfoLoaderTest {
    private Path jarWithoutDependencies;
    private Path jarWithDependencies;

    @Before
    public void setUp() throws Exception {
        String singleRoot = new File(getClass().getResource("/modules/single").getFile()).getAbsolutePath();
        jarWithoutDependencies = JarUtils.createZipJar(
                Paths.get(singleRoot, "content"),
                Paths.get(singleRoot, MANIFEST_NAME),
                Paths.get(singleRoot, "content", "module-info.class")
        );
        jarWithDependencies = JarUtils.createDirectoryJar(
                Paths.get(singleRoot, MANIFEST_NAME),
                Paths.get(singleRoot, "content")
        );
    }

    @Test
    public void loadJarInformation() {
        JarInfo jarInfo = JarInfoLoader.loadJarInformation(jarWithoutDependencies);
        Manifest manifest = jarInfo.getManifest();

        assertNotNull(jarInfo);
        assertNotNull(manifest);
        assertEquals("1.0.2", manifest.getMainAttributes().getValue(SPECIFICATION_VERSION));
        assertEquals("JarInfo{manifest=[Manifest-Version=1.0, SPECIFICATION_VERSION=1.0.2]}", jarInfo.toString());
    }

    @Test
    public void loadModuleInformation() {
        ModuleJarInfo moduleJarInfo = JarInfoLoader.loadModuleInformation(jarWithoutDependencies);
        Manifest manifest = moduleJarInfo.getManifest();
        ModuleDescriptor descriptor = moduleJarInfo.getDescriptor();

        assertNotNull(moduleJarInfo);
        assertNotNull(manifest);
        assertNotNull(descriptor);
        assertNull(moduleJarInfo.getDependencies());
        assertEquals("1.0.2", manifest.getMainAttributes().getValue(SPECIFICATION_VERSION));
        assertEquals("pl.mendroch.modularization", descriptor.toNameAndVersion());
        assertEquals("ModuleJarInfo{dependencies=null, descriptor=pl.mendroch.modularization, manifest=[Manifest-Version=1.0, SPECIFICATION_VERSION=1.0.2]}", moduleJarInfo.toString());
    }

    @Test
    public void loadJarWithDependenciesInformation() {
        JarInfo jarInfo = JarInfoLoader.loadJarInformation(jarWithDependencies);
        Manifest manifest = jarInfo.getManifest();

        assertNotNull(jarInfo);
        assertNotNull(manifest);
        assertEquals("1.0.2", manifest.getMainAttributes().getValue(SPECIFICATION_VERSION));
        assertEquals("JarInfo{manifest=[Manifest-Version=1.0, SPECIFICATION_VERSION=1.0.2]}", jarInfo.toString());
    }

    @Test
    public void loadModuleWithDependenciesInformation() {
        ModuleJarInfo moduleJarInfo = JarInfoLoader.loadModuleInformation(jarWithDependencies);
        Manifest manifest = moduleJarInfo.getManifest();
        ModuleDescriptor descriptor = moduleJarInfo.getDescriptor();
        Properties dependencies = moduleJarInfo.getDependencies();

        assertNotNull(moduleJarInfo);
        assertNotNull(manifest);
        assertNotNull(descriptor);
        assertNotNull(dependencies);
        assertEquals("1.0.2", manifest.getMainAttributes().getValue(SPECIFICATION_VERSION));
        assertEquals("pl.mendroch.modularization", descriptor.toNameAndVersion());
        assertEquals("{pl.mendroch.modularization-service=1.0.0}", dependencies.toString());
        assertEquals("ModuleJarInfo{dependencies={pl.mendroch.modularization-service=1.0.0}, descriptor=pl.mendroch.modularization, manifest=[Manifest-Version=1.0, SPECIFICATION_VERSION=1.0.2]}", moduleJarInfo.toString());
    }
}