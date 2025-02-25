package ru.task.slovo.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LockManagerTest {
    private LockManager lockManager;

    @BeforeEach
    void setUp() {
        lockManager = new LockManager();
    }

    @Test
    void testAcquireLockConcurrentAccess() throws InterruptedException {
        int key = 1;
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                lockManager.acquireLock(key);
                latch.countDown();
            });
        }

        latch.await();
        ReentrantCountedLock lock = lockManager.acquireLock(key);
        assertEquals(threadCount + 1, lock.getCount());
        executorService.shutdown();
    }

    @Test
    void testConcurrentAccessMultipleKeys() throws InterruptedException {
        int key1 = 1;
        int key2 = 2;
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount * 2);

        AtomicInteger key1Counter = new AtomicInteger(0);
        AtomicInteger key2Counter = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                lockManager.acquireLock(key1);
                key1Counter.incrementAndGet();
                latch.countDown();
            });

            executorService.submit(() -> {
                lockManager.acquireLock(key2);
                key2Counter.incrementAndGet();
                latch.countDown();
            });
        }

        latch.await();

        ReentrantCountedLock lock1 = lockManager.acquireLock(key1);
        ReentrantCountedLock lock2 = lockManager.acquireLock(key2);

        assertEquals(threadCount + 1, lock1.getCount());
        assertEquals(threadCount + 1, lock2.getCount());
        executorService.shutdown();
    }

}