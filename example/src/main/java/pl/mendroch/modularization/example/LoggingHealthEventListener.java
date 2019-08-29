package pl.mendroch.modularization.example;

import pl.mendroch.modularization.common.api.health.HealthEvent;
import pl.mendroch.modularization.common.api.health.HealthEventListener;

import java.util.List;

public class LoggingHealthEventListener implements HealthEventListener {
    @Override
    public void onEvents(List<HealthEvent> events) {
        for (HealthEvent event : events) {
            System.out.println(
                    event.getGroupName() + ":" + event.getId() + ":" + event.getName() + ":" + event.getTime()
                            + "\n\t" + event.getLevel() + ":" + event.getException().getMessage()
            );
            event.getException().printStackTrace();
        }
    }
}
