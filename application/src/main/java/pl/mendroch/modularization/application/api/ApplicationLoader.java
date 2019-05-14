package pl.mendroch.modularization.application.api;

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
import pl.mendroch.modularization.core.runtime.RuntimeManager;

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
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.application.internal.ApplicationArgumentName.LOADER;
import static pl.mendroch.modularization.application.internal.ApplicationArgumentName.PATH;
import static pl.mendroch.modularization.application.internal.util.ReflectionUtil.createInstanceWithOptionalParameters;
import static pl.mendroch.modularization.common.api.DependencyGraphUtils.createDependencyGraph;
import static pl.mendroch.modularization.common.api.JarInfoLoader.loadModulesInformation;
import static pl.mendroch.modularization.core.runtime.RuntimeManager.INSTANCE;

public class ApplicationLoader {
    private static final Logger LOGGER = Logger.getLogger(ApplicationLoader.class.getName());

    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<ApplicationArgumentName, String> parameters;
    private final CustomApplicationLoader loader;

    private final RuntimeManager runtimeManager = INSTANCE;

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
                LOGGER.info("Building dependency graph");
                Path directory = Paths.get(parameters.get(PATH));
                List<ModuleJarInfo> moduleJarInfos = loadModulesInformation(directory);

                Graph<ModuleJarInfo, Dependency> dependencyGraph = createDependencyGraph(moduleJarInfos);
                LOGGER.fine("Graph:" + System.lineSeparator() + dependencyGraph.toString());
                return new DependencyTreeBuilder(dependencyGraph);
            } catch (IOException e) {
                LOGGER.log(SEVERE, "Failed to build dependency graph", e);
                throw new IllegalStateException(e);
            }
        });
        DependencyTreeBuilder dependencyTreeBuilder = treeBuilderFuture.get();
        LOGGER.info("Building dependency tree finished");

        LOGGER.warning("Unused dependencies:" + dependencyTreeBuilder.getUnused());
        LOGGER.info("Obsolete dependencies:" + dependencyTreeBuilder.getObsolete());
        LOGGER.info("Building module graph");
        Node<ModuleJarInfo> root = dependencyTreeBuilder.getRoot();
        runtimeManager.setRoot(root);
        if (loader != null) loader.afterLoad();
    }

    public void run() {
        assert !runtimeManager.isInitialized() : "Application already started";
        runtimeManager.run();
    }

    private <T extends ApplicationConfigurator> List<T> loadServiceProviders(Class<T> aClass) {
        Map<ApplicationArgumentName, String> unmodifiableParameters = Collections.unmodifiableMap(parameters);
        return ServiceLoader.load(aClass).stream()
                .map(Provider::get)
                .peek(conf -> conf.setParameters(unmodifiableParameters))
                .collect(Collectors.toUnmodifiableList());
    }

    private void runConfigurations(List<? extends ApplicationConfigurator> configurators) {
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
