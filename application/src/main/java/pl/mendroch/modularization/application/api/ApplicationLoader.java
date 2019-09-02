package pl.mendroch.modularization.application.api;

import lombok.extern.java.Log;
import pl.mendroch.modularization.application.api.loaders.ApplicationConfigurator;
import pl.mendroch.modularization.application.api.loaders.ApplicationModuleLoader;
import pl.mendroch.modularization.application.api.loaders.CustomApplicationLoader;
import pl.mendroch.modularization.application.internal.ApplicationArgumentName;
import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.tree.Node;
import pl.mendroch.modularization.common.internal.concurrent.ConcurrencyUtil;
import pl.mendroch.modularization.core.DependencyTreeBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.application.internal.ApplicationArgumentName.LOADER;
import static pl.mendroch.modularization.application.internal.ApplicationArgumentName.PATH;
import static pl.mendroch.modularization.application.internal.util.ReflectionUtil.createInstanceWithOptionalParameters;
import static pl.mendroch.modularization.common.api.DependencyGraphUtils.createDependencyGraph;
import static pl.mendroch.modularization.common.internal.concurrent.ExceptionAwareThreadFactory.threadFactory;
import static pl.mendroch.modularization.core.runtime.ModuleFilesManager.MODULE_FILES_MANAGER;
import static pl.mendroch.modularization.core.runtime.OverrideManager.OVERRIDE_MANAGER;
import static pl.mendroch.modularization.core.runtime.RuntimeManager.RUNTIME_MANAGER;

@Log
public class ApplicationLoader {
    private final ExecutorService executor = newCachedThreadPool(threadFactory("application-loader"));
    private final Map<ApplicationArgumentName, String> parameters;
    private final CustomApplicationLoader loader;

    public ApplicationLoader(Map<ApplicationArgumentName, String> parameters) {
        this.parameters = parameters;
        loader = createInstanceWithOptionalParameters(CustomApplicationLoader.class, parameters.get(LOADER), Map.class, parameters);
    }

    public void loadCustomConfiguration() {
        List<ApplicationConfigurator> configurators = loadServiceProviders(ApplicationConfigurator.class);
        if (loader == null || loader.runConfigurators(configurators)) {
            runConfigurations(configurators);
        }
        List<ApplicationModuleLoader> moduleLoaders = loadServiceProviders(ApplicationModuleLoader.class);
        if (loader == null || loader.runModuleLoaders(moduleLoaders)) {
            runConfigurations(moduleLoaders);
        }
    }

    public void load() throws ExecutionException, InterruptedException {
        if (loader != null) loader.beforeLoad();
        Future<DependencyTreeBuilder> treeBuilderFuture = executor.submit(() -> {
            try {
                log.info("Building dependency graph");
                Path directory = Paths.get(parameters.get(PATH));
                MODULE_FILES_MANAGER.initialize(directory);
                List<ModuleJarInfo> moduleJarInfos = MODULE_FILES_MANAGER.getModules();

                Graph<ModuleJarInfo, Dependency> dependencyGraph = createDependencyGraph(moduleJarInfos, OVERRIDE_MANAGER.getOverrides());
                log.fine("Graph:" + System.lineSeparator() + dependencyGraph.toString());
                return new DependencyTreeBuilder(dependencyGraph);
            } catch (IOException e) {
                log.log(SEVERE, "Failed to build dependency graph", e);
                throw new IllegalStateException(e);
            }
        });
        DependencyTreeBuilder dependencyTreeBuilder = treeBuilderFuture.get();
        log.info("Building dependency tree finished");

        log.info("Third party dependencies:" + dependencyTreeBuilder.getThirdPartyJars());
        log.info("Obsolete dependencies:" + dependencyTreeBuilder.getObsolete());
        log.info("Building module graph");
        Node<ModuleJarInfo> root = dependencyTreeBuilder.getRoot();
        RUNTIME_MANAGER.initialize(root, dependencyTreeBuilder.getThirdPartyJars());
        if (loader != null) loader.afterLoad();
    }

    public void run() {
        assert !RUNTIME_MANAGER.isInitialized() : "Application is not initialized yet";
        RUNTIME_MANAGER.run();
    }

    private <T extends ApplicationConfigurator> List<T> loadServiceProviders(Class<T> aClass) {
        Map<ApplicationArgumentName, String> unmodifiableParameters = Collections.unmodifiableMap(parameters);
        return ServiceLoader.load(aClass).stream()
                .map(Provider::get)
                .peek(conf -> conf.setParameters(unmodifiableParameters))
                .collect(Collectors.toUnmodifiableList());
    }

    private void runConfigurations(List<? extends ApplicationConfigurator> configurators) {
        if (configurators.isEmpty()) return;

        //Submit first tasks
        submitTasks(
                configurators.parallelStream()
                        .filter(ApplicationConfigurator::shouldRunFirst)
                        .filter(ApplicationConfigurator::canRunInParallel)
        );
        submitTasks(
                configurators.stream()
                        .filter(ApplicationConfigurator::shouldRunFirst)
                        .filter(conf -> !conf.canRunInParallel())
        );

        //Submit tasks
        submitTasks(
                configurators.parallelStream()
                        .filter(conf -> !conf.shouldRunFirst())
                        .filter(ApplicationConfigurator::canRunInParallel)
                        .filter(conf -> !conf.shouldRunLast())
        );
        submitTasks(
                configurators.stream()
                        .filter(conf -> !conf.shouldRunFirst())
                        .filter(conf -> !conf.canRunInParallel())
                        .filter(conf -> !conf.shouldRunLast())
        );

        //Submit last tasks
        submitTasks(
                configurators.parallelStream()
                        .filter(conf -> !conf.shouldRunFirst())
                        .filter(ApplicationConfigurator::canRunInParallel)
                        .filter(ApplicationConfigurator::shouldRunLast)
        );
        submitTasks(
                configurators.stream()
                        .filter(conf -> !conf.shouldRunFirst())
                        .filter(conf -> !conf.canRunInParallel())
                        .filter(ApplicationConfigurator::shouldRunLast)
        );
    }

    private void submitTasks(Stream<? extends ApplicationConfigurator> collect) {
        collect.map(executor::submit).forEach(ConcurrencyUtil::awaitFuture);
    }
}
