package ru.task.slovo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.model.RequestDto;
import ru.task.slovo.util.LockManager;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RequestServiceImpl implements RequestService {

    private final Logger logger = LogManager.getLogger();
    private final Map<RequestDto.Type, ThreadPoolExecutor> executorMap;
    public final LockManager lockManager;

    public RequestServiceImpl() {
        lockManager = new LockManager();
        executorMap = new EnumMap<>(RequestDto.Type.class);

        for (RequestDto.Type type : RequestDto.Type.values()) {
            executorMap.put(type, new ThreadPoolExecutor(5, 5, 0L,
                    TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()));
        }
    }

    @Override
    public void submit(RequestDto request) {
        logQueueStates();
        RequestTask task = new RequestTask(request, lockManager);
        ThreadPoolExecutor executor = executorMap.get(request.getType());
        executor.submit(task);
    }

    private void logQueueStates() {
        executorMap.forEach((type, executor) -> logger.info("Состояние очереди для типа {}: {}", type, executor.getQueue().size()));
    }

    @Override
    public void shutdown() {
        executorMap.forEach((type, executor) -> executor.shutdown());

        executorMap.forEach((type, executor) -> {
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                logger.error("Ошибка при завершении ExecutorService для типа {}: {}", type, e.getMessage());
                executor.shutdownNow();
            }
        });
    }

    Map<RequestDto.Type, ThreadPoolExecutor> getExecutorMap() {
        return executorMap;
    }
}