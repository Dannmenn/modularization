package pl.mendroch.modularization.common.api.health;

import java.util.List;

public interface HealthEventListener {
    void onEvents(List<HealthEvent> events);
}
