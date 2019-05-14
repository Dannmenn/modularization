package pl.mendroch.modularization.core.runtime;

import pl.mendroch.modularization.common.api.annotation.TODO;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.tree.Node;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.api.health.HealthRegister.HEALTH_REGISTER;
import static pl.mendroch.modularization.common.internal.concurrent.ExceptionAwareThreadFactory.threadFactory;

public enum RuntimeManager {
    INSTANCE;
    private static final Logger LOGGER = Logger.getLogger(RuntimeManager.class.getName());

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final Map<ModuleJarInfo, ClassLoader> entryPoints = new HashMap<>();
    private final ExecutorService executor = newCachedThreadPool(threadFactory("runtime-manager"));

    public void setRoot(Node<ModuleJarInfo> root) {
        if (initialized.getAndSet(true)) {
            buildModulesGraph(root);
        } else {
            updateModulesGraph(root);
        }
    }

    @TODO
    private void updateModulesGraph(Node<ModuleJarInfo> root) {
        throw new UnsupportedOperationException("TODO");
    }

    @TODO
    private void buildModulesGraph(Node<ModuleJarInfo> root) {
    }

    public void run() {
        if (initialized.get()) {
            LOGGER.severe("Application already started. Update modules graph instead of reinitializing");
            throw new IllegalStateException("Application already started");
        }
        for (Entry<ModuleJarInfo, ClassLoader> entry : entryPoints.entrySet()) {
            executor.submit(() -> {
                try {
                    String mainClass = entry.getKey().getJarInfo().getMainClass();
                    Class<?> aClass = entry.getValue().loadClass(mainClass);
                    Method method = aClass.getMethod("main", String[].class);
                    //noinspection JavaReflectionInvocation
                    method.invoke(null);
                } catch (Exception e) {
                    LOGGER.log(SEVERE, e.getMessage(), e);
                    HEALTH_REGISTER.registerEvent(SEVERE, e);
                }
            });
        }
    }

    public boolean isInitialized() {
        return initialized.get();
    }
}
