package pl.mendroch.modularization.common.internal.health;

import pl.mendroch.modularization.common.api.health.HealthEvent;

import java.util.logging.Level;

public class HealthEventImpl implements HealthEvent {
    private final long time = System.currentTimeMillis();

    private final String groupName;
    private final String name;
    private final long id;
    private final Level level;
    private final Throwable exception;

    private boolean isNew;

    public HealthEventImpl(Thread thread, Level level, Throwable exception) {
        groupName = thread.getThreadGroup().getName();
        name = thread.getName();
        id = thread.getId();
        this.level = level;
        this.exception = exception;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public Throwable getException() {
        return exception;
    }

    public long getTime() {
        return time;
    }

    public boolean isNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
