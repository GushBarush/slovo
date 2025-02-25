package ru.task.slovo.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantCountedLock extends ReentrantLock{
    private final AtomicInteger count = new AtomicInteger(0);

    public ReentrantCountedLock() {
    }

    public ReentrantCountedLock(boolean fair) {
        super(fair);
    }

    public void increment() {
        count.incrementAndGet();
    }

    public int decrement() {
        return count.decrementAndGet();
    }

    public int getCount() {
        return count.get();
    }
}