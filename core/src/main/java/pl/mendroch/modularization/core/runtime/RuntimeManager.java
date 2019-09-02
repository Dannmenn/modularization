package pl.mendroch.modularization.core.runtime;

import lombok.extern.java.Log;
import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.tree.Node;
import pl.mendroch.modularization.core.DependencyTreeBuilder;
import pl.mendroch.modularization.core.graph.GraphFlattener;
import pl.mendroch.modularization.core.model.LoadedModuleReference;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.api.health.HealthRegister.HEALTH_REGISTER;
import static pl.mendroch.modularization.common.internal.concurrent.ExceptionAwareThreadFactory.threadFactory;
import static pl.mendroch.modularization.core.DependencyGraphUtils.createDependencyGraph;
import static pl.mendroch.modularization.core.runtime.ModuleFilesManager.MODULE_FILES_MANAGER;
import static pl.mendroch.modularization.core.runtime.OverrideManager.OVERRIDE_MANAGER;

@Log
public enum RuntimeManager {
    RUNTIME_MANAGER;
    private final List<RuntimeUpdateListener> listeners = new CopyOnWriteArrayList<>();
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final ExecutorService executor = newCachedThreadPool(threadFactory("runtime-manager"));
    private LoadedModuleReference parent;
    private LoadedModuleReference[] references;

    public void initialize(Node<ModuleJarInfo> root, Set<ModuleJarInfo> thirdPartyJars) {
        if (initialized.getAndSet(true)) {
            log.severe("Application already started. Update modules graph instead of reinitializing");
            throw new IllegalStateException("Application already started");
        } else {
            buildModulesGraph(root, thirdPartyJars);
        }
    }

    public void update(Dependency existing, Dependency override) throws Exception {
        OVERRIDE_MANAGER.override(existing, override);
        update();
    }

    public void update() throws Exception {
        Future<DependencyTreeBuilder> treeBuilderFuture = executor.submit(() -> {
            log.info("Building updated dependency graph");
            List<ModuleJarInfo> moduleJarInfos = MODULE_FILES_MANAGER.getModules();

            Graph<ModuleJarInfo, Dependency> dependencyGraph = createDependencyGraph(moduleJarInfos, OVERRIDE_MANAGER.getOverrides());
            log.fine("Updated graph:" + System.lineSeparator() + dependencyGraph.toString());
            return new DependencyTreeBuilder(dependencyGraph);
        });
        DependencyTreeBuilder dependencyTreeBuilder = treeBuilderFuture.get();
        log.info("Building updated dependency tree finished");

        log.info("Third party dependencies which won't be updated:" + dependencyTreeBuilder.getThirdPartyJars());
        log.info("Obsolete dependencies:" + dependencyTreeBuilder.getObsolete());
        updateRoot(dependencyTreeBuilder.getRoot());
    }

    private void updateRoot(Node<ModuleJarInfo> root) {
        List<ModuleJarInfo> flattened = new GraphFlattener<>(root).flatten();
        for (RuntimeUpdateListener listener : listeners) {
            listener.beforeUpdate();
        }
        references = new ClasspathUpdater(flattened).updateClassLoaders(references, parent);
        for (RuntimeUpdateListener listener : listeners) {
            listener.afterUpdate();
        }
    }

    private void buildModulesGraph(Node<ModuleJarInfo> root, Set<ModuleJarInfo> thirdPartyJars) {
        List<ModuleJarInfo> flattened = new GraphFlattener<>(root).flatten();
        ClasspathBuilder builder = new ClasspathBuilder(flattened);
        references = builder.buildClassLoaders(thirdPartyJars);
        parent = builder.getParent();
    }

    public void run() {
        if (started.getAndSet(true)) {
            log.severe("Application already started. Update modules graph instead of reinitializing");
            throw new IllegalStateException("Application already started");
        }
        final LoadedModuleReference entryPoint = references[0];
        executor.submit(() -> {
            try {
                String mainClass = entryPoint.getModule().getJarInfo().getMainClass();
                ClassLoader loader = entryPoint.getLoader();
                Thread.currentThread().setContextClassLoader(loader);
                Class<?> aClass = loader.loadClass(mainClass);
                Method method = aClass.getMethod("main", String[].class);
                method.invoke(null, new Object[]{new String[0]});
            } catch (Exception e) {
                log.log(SEVERE, e.getMessage(), e);
                HEALTH_REGISTER.registerEvent(SEVERE, e);
            }
        });
    }

    public boolean isInitialized() {
        return initialized.get();
    }

    public void addListener(RuntimeUpdateListener listener) {
        listeners.add(listener);
    }
}
