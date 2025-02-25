package ru.task.slovo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.model.RequestDto;
import ru.task.slovo.util.LockManager;
import ru.task.slovo.util.ReentrantCountedLock;

public class RequestTask implements Runnable {

    private final LockManager lockManager;
    private final Logger logger = LogManager.getLogger(RequestTask.class);
    private final RequestDto request;

    public RequestTask(RequestDto request, LockManager lockManager) {
        this.request = request;
        this.lockManager = lockManager;
    }

    @Override
    public void run() {
        int key = request.getX();
        ReentrantCountedLock countedLock = lockManager.acquireLock(key);
        countedLock.lock();
        try {
            logger.info("Начало обработки запроса {} с X = {} в потоке {}",
                    request.getType(), key, Thread.currentThread().getName());

            Thread.sleep(10000);

            logger.info("Завершение обработки запроса {} с X = {} в потоке {}",
                    request.getType(), key, Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            countedLock.unlock();
            lockManager.releaseLock(key, countedLock);
        }
    }

    RequestDto getRequest() {
        return request;
    }
}
