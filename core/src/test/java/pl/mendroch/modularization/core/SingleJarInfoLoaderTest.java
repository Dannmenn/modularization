package pl.mendroch.modularization.core;

import org.junit.Before;
import org.junit.Test;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.core.utils.JarUtils;

import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import static java.util.jar.JarFile.MANIFEST_NAME;
import static org.junit.Assert.*;

public class SingleJarInfoLoaderTest {
    private static final ModuleDescriptor EMPTY_MODULE = ModuleDescriptor.newModule("some").build();
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
        JarInfo jarInfo = JarInfoLoader.loadJarInformation(jarWithoutDependencies, EMPTY_MODULE);

        assertNotNull(jarInfo);
        assertEquals("1.0.2", jarInfo.getSpecificationVersion());
        assertTrue(jarInfo.toString().endsWith("1.0.2"));
    }

    @Test
    public void loadModuleInformation() {
        ModuleJarInfo moduleJarInfo = JarInfoLoader.loadModuleInformation(jarWithoutDependencies);
        ModuleDescriptor descriptor = moduleJarInfo.getDescriptor();
        JarInfo jarInfo = moduleJarInfo.getJarInfo();

        assertNotNull(moduleJarInfo);
        assertNotNull(descriptor);
        assertTrue(moduleJarInfo.getDependencyVersions().isEmpty());
        assertEquals("1.0.2", jarInfo.getSpecificationVersion());
        assertEquals("pl.mendroch.modularization", descriptor.toNameAndVersion());
        assertTrue(jarInfo.toString().endsWith("1.0.2"));
        assertEquals("pl.mendroch.modularization@1.0.2", moduleJarInfo.toString());
    }

    @Test
    public void loadJarWithDependenciesInformation() {
        JarInfo jarInfo = JarInfoLoader.loadJarInformation(jarWithDependencies, EMPTY_MODULE);

        assertNotNull(jarInfo);
        assertEquals("1.0.2", jarInfo.getSpecificationVersion());
        assertTrue(jarInfo.toString().endsWith("1.0.2"));
    }

    @Test
    public void loadModuleWithDependenciesInformation() {
        ModuleJarInfo moduleJarInfo = JarInfoLoader.loadModuleInformation(jarWithDependencies);
        JarInfo jarInfo = moduleJarInfo.getJarInfo();
        ModuleDescriptor descriptor = moduleJarInfo.getDescriptor();
        Properties dependencies = moduleJarInfo.getDependencyVersions();

        assertNotNull(moduleJarInfo);
        assertNotNull(descriptor);
        assertNotNull(dependencies);
        assertEquals("1.0.2", jarInfo.getSpecificationVersion());
        assertEquals("pl.mendroch.modularization", descriptor.toNameAndVersion());
        assertEquals("{pl.mendroch.modularization:service=1.0.0}", dependencies.toString());
        assertTrue(jarInfo.toString().endsWith("1.0.2"));
        assertEquals("pl.mendroch.modularization@1.0.2", moduleJarInfo.toString());
    }
}