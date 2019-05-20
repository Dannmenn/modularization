package pl.mendroch.modularization.core.runtime;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.tree.Node;
import pl.mendroch.modularization.core.DependencyTreeBuilder;
import pl.mendroch.modularization.core.graph.GraphFlattener;
import pl.mendroch.modularization.core.model.LoadedModuleReference;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.api.DependencyGraphUtils.createDependencyGraph;
import static pl.mendroch.modularization.common.api.health.HealthRegister.HEALTH_REGISTER;
import static pl.mendroch.modularization.common.api.utils.TODO.TODO;
import static pl.mendroch.modularization.common.internal.concurrent.ExceptionAwareThreadFactory.threadFactory;
import static pl.mendroch.modularization.core.runtime.ModuleFilesManager.MODULE_FILES_MANAGER;
import static pl.mendroch.modularization.core.runtime.OverrideManager.OVERRIDE_MANAGER;

public enum RuntimeManager {
    RUNTIME_MANAGER;
    private static final Logger LOGGER = Logger.getLogger(RuntimeManager.class.getName());

    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final ExecutorService executor = newCachedThreadPool(threadFactory("runtime-manager"));
    private LoadedModuleReference entryPoint;

    public void initialize(Node<ModuleJarInfo> root) {
        if (initialized.getAndSet(true)) {
            LOGGER.severe("Application already started. Update modules graph instead of reinitializing");
            throw new IllegalStateException("Application already started");
        } else {
            buildModulesGraph(root);
        }
    }

    public void update(Dependency existing, Dependency override) throws Exception {
        OVERRIDE_MANAGER.override(existing, override);
        Future<DependencyTreeBuilder> treeBuilderFuture = executor.submit(() -> {
            LOGGER.info("Building updated dependency graph");
            List<ModuleJarInfo> moduleJarInfos = MODULE_FILES_MANAGER.getModules();

            Graph<ModuleJarInfo, Dependency> dependencyGraph = createDependencyGraph(moduleJarInfos, OVERRIDE_MANAGER.getOverrides());
            LOGGER.fine("Updated graph:" + System.lineSeparator() + dependencyGraph.toString());
            return new DependencyTreeBuilder(dependencyGraph);
        });
        DependencyTreeBuilder dependencyTreeBuilder = treeBuilderFuture.get();
        LOGGER.info("Building updated dependency tree finished");

        LOGGER.info("Unused dependencies:" + dependencyTreeBuilder.getUnused());
        LOGGER.info("Obsolete dependencies:" + dependencyTreeBuilder.getObsolete());
        updateRoot(dependencyTreeBuilder.getRoot());
    }

    private void updateRoot(Node<ModuleJarInfo> root) {
        TODO("Implement");
    }

    private void buildModulesGraph(Node<ModuleJarInfo> root) {
        List<ModuleJarInfo> flattened = new GraphFlattener<>(root).flatten();
        LoadedModuleReference[] references = new ClasspathBuilder(flattened).buildClassLoaders();
        entryPoint = references[0];
    }

    public void run() {
        if (started.getAndSet(true)) {
            LOGGER.severe("Application already started. Update modules graph instead of reinitializing");
            throw new IllegalStateException("Application already started");
        }
        executor.submit(() -> {
            try {
                String mainClass = entryPoint.getModule().getJarInfo().getMainClass();
                ClassLoader loader = entryPoint.getLoader();
                Thread.currentThread().setContextClassLoader(loader);
                Class<?> aClass = loader.loadClass(mainClass);
                Method method = aClass.getMethod("main", String[].class);
                method.invoke(null, new Object[]{new String[0]});
            } catch (Exception e) {
                LOGGER.log(SEVERE, e.getMessage(), e);
                HEALTH_REGISTER.registerEvent(SEVERE, e);
            }
        });
    }

    public boolean isInitialized() {
        return initialized.get();
    }
}
