package pl.mendroch.modularization.application.api.loaders;

import pl.mendroch.modularization.application.internal.ApplicationArgumentName;

import java.util.Map;

public interface ApplicationConfigurator extends Runnable {
    default boolean shouldRunFirst() {
        return false;
    }

    default boolean canRunInParallel() {
        return true;
    }

    default boolean shouldRunLast() {
        return false;
    }

    void setParameters(Map<ApplicationArgumentName, String> parameters);
}
