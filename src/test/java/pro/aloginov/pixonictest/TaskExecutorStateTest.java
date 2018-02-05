package pro.aloginov.pixonictest;

import org.junit.Before;
import org.junit.Test;

import static java.time.LocalDateTime.now;

public class TaskExecutorStateTest {

    private TaskExecutor taskExecutor;

    @Before
    public void init() {
        taskExecutor = new TaskExecutor();
    }

    @Test(expected = IllegalStateException.class)
    public void testAddNotStarted() {
        taskExecutor.add(now(), () -> null);
    }

    @Test
    public void testStartNotYetStarted() {
        taskExecutor.start();
    }

    @Test(expected = IllegalStateException.class)
    public void testStartAlreadyStarted() {
        taskExecutor.start();
        taskExecutor.start();
    }

    @Test(expected = IllegalStateException.class)
    public void testShutdownNotStarted() {
        taskExecutor.shutdown();
    }

    @Test
    public void testShutdown() {
        taskExecutor.start();
        taskExecutor.shutdown();
    }

    @Test(expected = IllegalStateException.class)
    public void testRestartAfterShutdown() {
        taskExecutor.start();
        taskExecutor.shutdown();
        taskExecutor.start();
    }

    @Test(expected = IllegalStateException.class)
    public void testAddTaskAfterShutdown() {
        taskExecutor.start();
        taskExecutor.shutdown();
        taskExecutor.add(now(), () -> null);
    }

}
