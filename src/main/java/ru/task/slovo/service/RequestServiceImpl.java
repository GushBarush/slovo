package ru.task.slovo.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.model.RequestDto;
import ru.task.slovo.util.LockManager;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.*;

public class RequestServiceImpl implements RequestService {

    private final Logger logger = LogManager.getLogger(RequestServiceImpl.class);
    private final Map<RequestDto.Type, ExecutorService> executorMap;
    public final LockManager lockManager;

    public RequestServiceImpl() {
        lockManager = new LockManager();
        executorMap = new EnumMap<>(RequestDto.Type.class);

        for (RequestDto.Type type : RequestDto.Type.values()) {
            executorMap.put(type, ForkJoinPool.commonPool());
        }
    }

    @Override
    public void submit(RequestDto request) {
        RequestTask task = new RequestTask(request, lockManager);
        ExecutorService executor = executorMap.get(request.getType());
        executor.submit(task);
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
                Thread.currentThread().interrupt();
            }
        });
    }

    Map<RequestDto.Type, ExecutorService> getExecutorMap() {
        return executorMap;
    }
}