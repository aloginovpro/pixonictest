package pro.aloginov.pixonictest;

import java.time.LocalDateTime;
import java.util.concurrent.*;

import static java.time.LocalDateTime.now;

/**
 * To start using task executor call {@link #start}
 * To schedule new task call {@link #add}, scheduleTime can be in the past. (executed under lock)
 * No Future<> is returned so you can never know if the task has been executed or has failed
 * Be aware task queue size is not checked. Do it on your own calling {@link #size}. (uses same lock as adding)
 * After you finish call {@link #shutdown}
 * */

public class TaskExecutor {

    private final DelayQueue<ScheduledTask> queue = new DelayQueue<>();
    //use single thread executor since it is specified to execute tasks in strict total order
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final TakeAndExecuteTask takeFromQueueAndExecute = new TakeAndExecuteTask();
    private volatile State state = State.NOT_STARTED;

    public void start() {
        if (state != State.NOT_STARTED) {
            throw new IllegalStateException("Task executor can not be reused");
        }
        state = State.STARTED;
        executor.submit(takeFromQueueAndExecute);
    }

    public void shutdown() {
        if (state != State.STARTED) {
            throw new IllegalStateException("Task executor is not started");
        }
        state = State.FINISHED;
        executor.shutdownNow();
    }

    public final void add(LocalDateTime scheduledTime, Callable task) {
        if (state != State.STARTED) {
            throw new IllegalStateException("Task executor is not started");
        }
        if (scheduledTime == null) {
            throw new NullPointerException("scheduledTime");
        }
        if (task == null) {
            throw new NullPointerException("task");
        }
        queue.put(new ScheduledTask(now(), scheduledTime, task));
    }

    public final int size() {
        return queue.size();
    }

    private class TakeAndExecuteTask implements Runnable {
        @Override
        public void run() {
            try {
                ScheduledTask scheduledTask = queue.take();
                executor.submit(scheduledTask.task);
                executor.submit(takeFromQueueAndExecute);
            } catch (InterruptedException ignored) {}
        }
    }

    private enum State { NOT_STARTED, STARTED, FINISHED }

}
