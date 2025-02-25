package ru.task.slovo.util;

import java.util.concurrent.ConcurrentHashMap;

public class LockManager {

    private final ConcurrentHashMap<Integer, ReentrantCountedLock> locks = new ConcurrentHashMap<>();

    public ReentrantCountedLock acquireLock(int key) {
        return locks.compute(key, (k, livingLock) -> {
            if (livingLock == null) {
                livingLock = new ReentrantCountedLock(true);
            }
            livingLock.increment();
            return livingLock;
        });
    }

    public void releaseLock(int key, ReentrantCountedLock countedLock) {
        int count = countedLock.decrement();
        if (count == 0 && !countedLock.isLocked()) {
            locks.remove(key, countedLock);
        }
    }

    ConcurrentHashMap<Integer, ReentrantCountedLock> getLocks() {
        return locks;
    }
}