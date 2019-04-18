package pl.mendroch.modularization.common.api;

import org.junit.Test;
import pl.mendroch.modularization.common.api.model.ModuleJarInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static pl.mendroch.modularization.common.api.JarInfoLoader.loadModulesInformation;

public class DirectoryJarInfoLoaderTest {
    @Test
    public void loadDirectoryWithMultipleModuleJar() throws IOException {
        Path directory = new File(getClass().getResource("/libs").getFile()).toPath();
        List<ModuleJarInfo> moduleJarInfos = loadModulesInformation(directory);
        moduleJarInfos.sort(Comparator.comparing(o -> o.getDescriptor().toNameAndVersion()));
        assertEquals(4, moduleJarInfos.size());
        validateModuleInfo(moduleJarInfos.get(0),
                "pl.mendroch.modularization.example.main",
                "[org.apache.commons:commons-lang3@3.3.2, pl.mendroch.modularization.test:provider@1.0-SNAPSHOT]");
        validateModuleInfo(moduleJarInfos.get(1),
                "pl.mendroch.modularization.example.provider",
                "[org.apache.commons:commons-lang3@3.1, pl.mendroch.modularization.test:service@1.0-SNAPSHOT]");
        validateModuleInfo(moduleJarInfos.get(2),
                "pl.mendroch.modularization.example.provider",
                "[org.apache.commons:commons-lang3@3.3.2, pl.mendroch.modularization.test:service@1.0-SNAPSHOT]");
        validateModuleInfo(moduleJarInfos.get(3),
                "pl.mendroch.modularization.example.service",
                "[]");
    }

    private void validateModuleInfo(ModuleJarInfo moduleJarInfo, String expectedName, String expectedDependencies) {
        assertEquals(expectedName, moduleJarInfo.getDescriptor().toNameAndVersion());
        assertEquals(expectedDependencies, String.valueOf(moduleJarInfo.getDependencies()));
    }
}
