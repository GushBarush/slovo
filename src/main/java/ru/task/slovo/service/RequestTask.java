package ru.task.slovo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.model.RequestDto;
import ru.task.slovo.util.LockManager;
import ru.task.slovo.util.ReentrantCountedLock;

public class RequestTask implements Runnable {

    private final Logger logger = LogManager.getLogger();
    private final RequestDto request;

    public RequestTask(RequestDto request) {
        this.request = request;
    }

    @Override
    public void run() {
        int key = request.getX();
        ReentrantCountedLock countedLock = LockManager.acquireLock(key);
        countedLock.lock();
        try {
            logger.info("Начало обработки запроса {} с X = {} в потоке {}",
                    request.getType(), key, Thread.currentThread().getName());

            Thread.sleep(10000);

            logger.info("Завершение обработки запроса {} с X = {} в потоке {}",
                    request.getType(), key, Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        } finally {
            countedLock.unlock();
            LockManager.releaseLock(key, countedLock);
        }
    }
}
