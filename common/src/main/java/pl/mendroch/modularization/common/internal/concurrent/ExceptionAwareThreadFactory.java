package pl.mendroch.modularization.common.internal.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.api.health.HealthRegister.HEALTH_REGISTER;

public class ExceptionAwareThreadFactory implements ThreadFactory {
    private static final Logger LOGGER = Logger.getLogger(ExceptionAwareThreadFactory.class.getName());

    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String name;

    private ExceptionAwareThreadFactory(String name) {
        this.name = name;
    }

    public static ThreadFactory threadFactory(String name) {
        return new ExceptionAwareThreadFactory(name);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name + "-" + threadNumber.getAndIncrement());
        thread.setUncaughtExceptionHandler((t, e) -> {
            LOGGER.log(SEVERE, "Uncaught exception:" + t.getName() + ":" + e.getMessage(), e);
            HEALTH_REGISTER.registerEvent(t, SEVERE, e);
        });
        return thread;
    }
}
