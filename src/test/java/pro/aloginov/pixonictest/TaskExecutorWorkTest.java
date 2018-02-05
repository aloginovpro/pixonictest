package pro.aloginov.pixonictest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;

public class TaskExecutorWorkTest {

    private TaskExecutor taskExecutor;
    private AtomicInteger executionResult;

    @Before
    public void init() {
        taskExecutor = new TaskExecutor();
        taskExecutor.start();
        executionResult = new AtomicInteger(0);
    }

    @After
    public void after() {
        taskExecutor.shutdown();
    }

    @Test
    public void singleTaskTest() throws InterruptedException {
        assertEquals(0, taskExecutor.size());

        taskExecutor.add(now().plusSeconds(1), () -> {
            executionResult.set(1);
            return null;
        });
        assertEquals(1, taskExecutor.size());

        Thread.sleep(2000); //hope test will not blink :S
        assertEquals(0, taskExecutor.size());
        assertEquals(1, executionResult.get());
    }

    @Test
    public void differentTimeTaskTest() throws InterruptedException {
        LocalDateTime now = now();

        taskExecutor.add(now.plusSeconds(2), () -> {
            executionResult.set(2);
            return null;
        });

        taskExecutor.add(now.plusSeconds(1), () -> {
            executionResult.set(1);
            return null;
        });

        Thread.sleep(3000);
        assertEquals(2, executionResult.get());
    }

    @Test
    public void sameTimeTaskTest() throws InterruptedException {
        LocalDateTime time = now().plusSeconds(2);

        taskExecutor.add(time, () -> {
            executionResult.set(1);
            return null;
        });

        Thread.sleep(1000);

        taskExecutor.add(time, () -> {
            executionResult.set(2);
            return null;
        });

        Thread.sleep(2000);
        assertEquals(2, executionResult.get());
    }


}
