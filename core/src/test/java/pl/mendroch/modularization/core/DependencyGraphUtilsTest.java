package pl.mendroch.modularization.core;

import org.junit.Test;
import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static pl.mendroch.modularization.core.DependencyGraphUtils.createDependencyGraph;
import static pl.mendroch.modularization.core.JarInfoLoader.loadModulesInformation;

public class DependencyGraphUtilsTest {
    private static final String PROVIDER_1_0 = "pl.mendroch.example.modularization.provider@1.0-SNAPSHOT";
    private static final String EXAMPLE_1_0 = "pl.mendroch.example.modularization.main@1.0-SNAPSHOT";
    private static final String PROVIDER_1_1 = "pl.mendroch.example.modularization.provider@1.1-SNAPSHOT";
    private static final String SERVICE_1_0 = "pl.mendroch.example.modularization.service@1.0-SNAPSHOT";

    @Test
    public void validateDependencyGraph() throws Exception {
        Path directory = new File(getClass().getResource("/libs").getFile()).toPath();
        List<ModuleJarInfo> moduleJarInfos = loadModulesInformation(directory);

        Graph graph = createDependencyGraph(moduleJarInfos, new HashMap<>());

        assertNotNull(graph);
        assertEquals(
                PROVIDER_1_0 + "->[" + SERVICE_1_0 + "]\n" +
                        EXAMPLE_1_0 + "->[" + SERVICE_1_0 + ", " + PROVIDER_1_0 + ", " + PROVIDER_1_1 + "]\n" +
                        PROVIDER_1_1 + "->[" + SERVICE_1_0 + "]\n" +
                        SERVICE_1_0 + "->[]\n",
                graph.toString());
    }

    @Test
    public void validateDependencyGraphWithOverride() throws Exception {
        Path directory = new File(getClass().getResource("/libs").getFile()).toPath();
        List<ModuleJarInfo> moduleJarInfos = loadModulesInformation(directory);

        Graph graph = createDependencyGraph(moduleJarInfos,
                Map.of(
                        new Dependency("pl.mendroch.example.modularization.provider", "1.0-SNAPSHOT"),
                        new Dependency("pl.mendroch.example.modularization.provider", "1.1-SNAPSHOT")));

        assertNotNull(graph);
        assertEquals(
                EXAMPLE_1_0 + "->[" + PROVIDER_1_1 + ", " + SERVICE_1_0 + "]\n" +
                        PROVIDER_1_1 + "->[" + SERVICE_1_0 + "]\n" +
                        SERVICE_1_0 + "->[]\n",
                graph.toString());
    }
}