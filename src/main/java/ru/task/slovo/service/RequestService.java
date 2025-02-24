package ru.task.slovo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.model.RequestDto;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestService {

    private final Logger logger = LogManager.getLogger();
    private final ThreadPoolExecutor executorA;
    private final ThreadPoolExecutor executorB;

    public RequestService() {
        executorA = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        executorB = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    public void submit(RequestDto request) {
        logQueueStates();
        RequestTask task = new RequestTask(request);
        if (request.getType().equals(RequestDto.Type.A)) {
            executorA.submit(task);
        } else {
            executorB.submit(task);
        }
    }

    private void logQueueStates() {
        logger.info("Состояние очередей: Очередь A: {}, Очередь B: {}",
                executorA.getQueue().size(), executorB.getQueue().size());
    }

    public void shutdown() {
        executorA.shutdown();
        executorB.shutdown();
    }
}