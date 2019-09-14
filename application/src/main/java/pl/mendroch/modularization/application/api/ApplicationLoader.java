package pl.mendroch.modularization.application.api;

import lombok.extern.java.Log;
import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.core.AnalyzedGraph;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.application.api.ApplicationArgumentName.PATH;
import static pl.mendroch.modularization.common.api.concurrent.ExceptionAwareThreadFactory.threadFactory;
import static pl.mendroch.modularization.core.DependencyGraphUtils.createDependencyGraph;
import static pl.mendroch.modularization.core.runtime.ModuleFilesManager.MODULE_FILES_MANAGER;
import static pl.mendroch.modularization.core.runtime.OverrideManager.OVERRIDE_MANAGER;
import static pl.mendroch.modularization.core.runtime.RuntimeManager.RUNTIME_MANAGER;

@Log
public class ApplicationLoader {
    private final ExecutorService executor = newCachedThreadPool(threadFactory("application-loader"));
    private final Map<ApplicationArgumentName, String> parameters;

    public ApplicationLoader(Map<ApplicationArgumentName, String> parameters) {
        this.parameters = parameters;
    }

    public void load() throws ExecutionException, InterruptedException {
        Future<AnalyzedGraph> treeBuilderFuture = executor.submit(() -> {
            try {
                log.info("Building dependency graph");
                Path directory = Paths.get(parameters.get(PATH));
                MODULE_FILES_MANAGER.initialize(directory);
                List<ModuleJarInfo> moduleJarInfos = MODULE_FILES_MANAGER.getModules();

                Graph dependencyGraph = createDependencyGraph(moduleJarInfos, OVERRIDE_MANAGER.getOverrides());
                log.fine("Graph:" + System.lineSeparator() + dependencyGraph.toString());
                return new AnalyzedGraph(dependencyGraph);
            } catch (IOException e) {
                log.log(SEVERE, "Failed to build dependency graph", e);
                throw new IllegalStateException(e);
            }
        });
        AnalyzedGraph analyzedGraph = treeBuilderFuture.get();
        log.info("Building dependency tree finished");

        log.info("Third party dependencies:" + analyzedGraph.getThirdPartyJars());
        log.info("Obsolete dependencies:" + analyzedGraph.getObsolete());
        log.info("Building module graph");
        RUNTIME_MANAGER.initialize(analyzedGraph.getFlattened(), analyzedGraph.getThirdPartyJars());
    }

    public void run() {
        assert !RUNTIME_MANAGER.isInitialized() : "Application is not initialized yet";
        RUNTIME_MANAGER.run();
    }
}
