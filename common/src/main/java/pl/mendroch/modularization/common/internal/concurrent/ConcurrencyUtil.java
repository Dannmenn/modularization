package pl.mendroch.modularization.common.internal.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class ConcurrencyUtil {
    private ConcurrencyUtil() {
        //Hide implicit constructor
    }

    public static void awaitFuture(Future<?> future) {
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException(e);
        }
    }
}
