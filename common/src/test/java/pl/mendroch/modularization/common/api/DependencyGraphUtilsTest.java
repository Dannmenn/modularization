package pl.mendroch.modularization.common.api;

import org.junit.Test;
import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static pl.mendroch.modularization.common.api.DependencyGraphUtils.createDependencyGraph;
import static pl.mendroch.modularization.common.api.JarInfoLoader.loadModulesInformation;

public class DependencyGraphUtilsTest {
    private static final String PROVIDER_1_0 = "pl.mendroch.modularization.test:provider@1.0-SNAPSHOT";
    private static final String EXAMPLE_1_0 = "pl.mendroch.modularization.test:example@1.0-SNAPSHOT";
    private static final String PROVIDER_1_1 = "pl.mendroch.modularization.test:provider@1.1-SNAPSHOT";
    private static final String COMMONS_LANG_3_1 = "org.apache.commons:commons-lang3@3.1";
    private static final String COMMONS_LANG_3_3 = "org.apache.commons:commons-lang3@3.3.2";
    private static final String SERVICE_1_0 = "pl.mendroch.modularization.test:service@1.0-SNAPSHOT";

    @Test
    public void validateDependencyGraph() throws IOException {
        Path directory = new File(getClass().getResource("/libs").getFile()).toPath();
        List<ModuleJarInfo> moduleJarInfos = loadModulesInformation(directory);

        Graph<Dependency> graph = createDependencyGraph(moduleJarInfos);

        assertNotNull(graph);
        assertEquals(
                PROVIDER_1_0 + "->[" + COMMONS_LANG_3_1 + ", " + SERVICE_1_0 + "]\n" +
                        SERVICE_1_0 + "->[]\n" +
                        EXAMPLE_1_0 + "->[" + COMMONS_LANG_3_3 + ", " + PROVIDER_1_0 + "]\n" +
                        PROVIDER_1_1 + "->[" + COMMONS_LANG_3_3 + ", " + SERVICE_1_0 + "]\n",
                graph.toString());
    }
}