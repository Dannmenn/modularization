package pl.mendroch.modularization.common.api.concurrent;

import lombok.extern.java.Log;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.api.health.HealthRegister.HEALTH_REGISTER;

@Log
public class ExceptionAwareThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String name;

    ExceptionAwareThreadFactory(String name) {
        this.name = name;
    }

    public static ThreadFactory threadFactory(String name) {
        return new ExceptionAwareThreadFactory(name);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r, name + "-" + threadNumber.getAndIncrement());
        thread.setUncaughtExceptionHandler((t, e) -> {
            log.log(SEVERE, "Uncaught exception:" + t.getName() + ":" + e.getMessage(), e);
            HEALTH_REGISTER.registerEvent(t, SEVERE, e);
        });
        return thread;
    }
}
