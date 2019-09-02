package pl.mendroch.modularization.common.internal.concurrent;

import lombok.extern.java.Log;

import java.util.concurrent.ThreadFactory;

@Log
public class DaemonExceptionAwareThreadFactory extends ExceptionAwareThreadFactory {
    private DaemonExceptionAwareThreadFactory(String name) {
        super(name);
    }

    public static ThreadFactory daemonThreadFactory(String name) {
        return new DaemonExceptionAwareThreadFactory(name);
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = super.newThread(r);
        thread.setDaemon(true);
        return thread;
    }
}
