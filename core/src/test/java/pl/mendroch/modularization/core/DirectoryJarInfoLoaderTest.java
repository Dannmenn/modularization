package pl.mendroch.modularization.core;

import org.junit.Test;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static pl.mendroch.modularization.core.JarInfoLoader.loadModulesInformation;

public class DirectoryJarInfoLoaderTest {
    @Test
    public void loadDirectoryWithMultipleModuleJar() throws IOException {
        Path directory = new File(getClass().getResource("/libs").getFile()).toPath();
        List<ModuleJarInfo> moduleJarInfos = loadModulesInformation(directory);
        moduleJarInfos.sort(Comparator.comparing(o -> o.getDescriptor().toNameAndVersion()));
        assertEquals(4, moduleJarInfos.size());
        validateModuleInfo(moduleJarInfos.get(0), "pl.mendroch.example.modularization.main");
        validateModuleInfo(moduleJarInfos.get(1), "pl.mendroch.example.modularization.provider");
        validateModuleInfo(moduleJarInfos.get(2), "pl.mendroch.example.modularization.provider");
        validateModuleInfo(moduleJarInfos.get(3), "pl.mendroch.example.modularization.service");
    }

    private void validateModuleInfo(ModuleJarInfo moduleJarInfo, String expectedName) {
        assertEquals(expectedName, moduleJarInfo.getDescriptor().toNameAndVersion());
    }
}
