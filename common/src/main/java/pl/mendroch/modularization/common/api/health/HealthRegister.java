package pl.mendroch.modularization.common.api.health;

import pl.mendroch.modularization.common.internal.health.HealthEventImpl;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import static java.util.concurrent.TimeUnit.DAYS;
import static pl.mendroch.modularization.common.internal.concurrent.ConcurrencyUtil.awaitFuture;

public enum HealthRegister {
    HEALTH_REGISTER;
    private static final long KEEP_HISTORY_DELAY = Duration.ofDays(1).toMillis();
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(0);
    private final Map<Level, List<HealthEvent>> events = new HashMap<>();
    private Future<?> notifyFuture;

    HealthRegister() {
        service.scheduleAtFixedRate(this::cleanup, 1, 1, DAYS);
    }

    public synchronized void registerEvent(Level level, Throwable ex) {
        registerEvent(Thread.currentThread(), level, ex);
    }

    public synchronized void registerEvent(Thread thread, Level level, Throwable ex) {
        List<HealthEvent> events = this.events.computeIfAbsent(level, l -> new LinkedList<>());
        events.add(new HealthEventImpl(thread, level, ex));
        if (notifyFuture != null && !notifyFuture.isDone()) {
            Future<?> tmp = this.notifyFuture;
            this.notifyFuture = service.submit(() -> {
                awaitFuture(tmp);
                notifyListeners();
            });
        }
    }

    private void notifyListeners() {
        List<HealthEvent> newEvents = new ArrayList<>();
        for (List<HealthEvent> values : events.values()) {
            for (HealthEvent value : values) {
                HealthEventImpl healthEvent = (HealthEventImpl) value;
                if (healthEvent.isNew()) {
                    newEvents.add(value);
                    healthEvent.setIsNew(false);
                }
            }
        }
        newEvents.sort(Comparator.comparingLong(HealthEvent::getTime));
        List<HealthEvent> eventsToSend = Collections.unmodifiableList(newEvents);
        ServiceLoader<HealthEventListener> loader = ServiceLoader.load(HealthEventListener.class);
        for (HealthEventListener listener : loader) {
            listener.onEvents(eventsToSend);
        }
    }

    private synchronized void cleanup() {
        long now = System.currentTimeMillis();
        events
                .entrySet()
                .removeIf(entry ->
                        entry.getValue().removeIf(event -> now - event.getTime() > KEEP_HISTORY_DELAY)
                );
    }
}
