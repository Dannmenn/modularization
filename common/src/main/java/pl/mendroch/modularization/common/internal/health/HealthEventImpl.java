package pl.mendroch.modularization.common.internal.health;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.mendroch.modularization.common.api.health.HealthEvent;

import java.util.logging.Level;

@Getter
@ToString
public class HealthEventImpl implements HealthEvent {
    private final long time = System.currentTimeMillis();

    private final String groupName;
    private final String name;
    private final long id;
    private final Level level;
    private final Throwable exception;

    @Setter
    private boolean isNew;

    public HealthEventImpl(Thread thread, Level level, Throwable exception) {
        groupName = thread.getThreadGroup().getName();
        name = thread.getName();
        id = thread.getId();
        this.level = level;
        this.exception = exception;
    }
}
