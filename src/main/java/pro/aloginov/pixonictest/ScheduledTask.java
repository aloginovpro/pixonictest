package pro.aloginov.pixonictest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.time.LocalDateTime.now;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class ScheduledTask implements Delayed {

    private final LocalDateTime createdTime;
    private final LocalDateTime scheduledTime;
    public final Callable<?> task;

    public ScheduledTask(LocalDateTime createdTime, LocalDateTime scheduledTime, Callable<?> task) {
        this.createdTime = createdTime;
        this.scheduledTime = scheduledTime;
        this.task = task;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(Duration.between(now(), scheduledTime).toMillis(), MILLISECONDS);
    }

    /* if tasks are scheduled for the same time and at the same moment, well, let them be executed in random order
     * since sorting is not stable.
     * otherwise have to use some incremented counter instead of scheduledTime
    */
    @Override
    public int compareTo(Delayed o) {
        if (o instanceof ScheduledTask) {
            ScheduledTask other = (ScheduledTask) o;
            int scheduled = scheduledTime.compareTo(other.scheduledTime);
            return scheduled != 0 ? scheduled : createdTime.compareTo(other.createdTime);
        }
        throw new UnsupportedOperationException();
    }

}
