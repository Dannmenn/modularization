package pl.mendroch.modularization.common.api.health;

import java.util.logging.Level;

public interface HealthEvent {

    String getGroupName();

    String getName();

    long getId();

    Level getLevel();

    Throwable getException();

    boolean isNew();

    long getTime();
}
