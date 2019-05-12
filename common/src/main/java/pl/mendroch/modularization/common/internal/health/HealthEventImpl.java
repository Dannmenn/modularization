package pl.mendroch.modularization.common.internal.health;

import pl.mendroch.modularization.common.api.annotation.TODO;
import pl.mendroch.modularization.common.api.health.HealthEvent;

import java.util.logging.Level;

@TODO
public class HealthEventImpl implements HealthEvent {
    public HealthEventImpl(Thread thread, Level level, Throwable ex) {

    }

    public long getTime() {
        return 0;
    }

    public boolean isNew() {
        return false;
    }

    public void setIsNew(boolean isNew) {

    }
}
