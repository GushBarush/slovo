package ru.task.slovo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import ru.task.slovo.model.RequestDto;

import java.util.Map;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RequestServiceImplTest {

    private RequestServiceImpl requestService;
    private Map<RequestDto.Type, ExecutorService> mockExecutorMap;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl();
        mockExecutorMap = requestService.getExecutorMap();

        for (RequestDto.Type type : RequestDto.Type.values()) {
            ForkJoinPool mockExecutor = mock(ForkJoinPool.class);
            mockExecutorMap.put(type, mockExecutor);
        }
    }

    @ParameterizedTest
    @EnumSource(RequestDto.Type.class)
    void testSubmitShouldSubmitTaskForValidRequestType(RequestDto.Type type) {
        RequestDto request = new RequestDto(type, 1);

        requestService.submit(request);

        verify(mockExecutorMap.get(type), times(1)).submit(any(RequestTask.class));
    }

    @ParameterizedTest
    @EnumSource(RequestDto.Type.class)
    void testSubmitShouldSendCorrectTaskToQueue(RequestDto.Type type) {
        RequestDto request = new RequestDto(type, 1);
        ExecutorService mockExecutor = mockExecutorMap.get(type);
        ArgumentCaptor<RequestTask> taskCaptor = ArgumentCaptor.forClass(RequestTask.class);

        requestService.submit(request);

        verify(mockExecutor, times(1)).submit(taskCaptor.capture());
        RequestTask capturedTask = taskCaptor.getValue();
        assertEquals(request, capturedTask.getRequest());
    }

    @Test
    void testShutdownShouldShutdownAllExecutors() throws InterruptedException {
        requestService.shutdown();

        for (ExecutorService executor : mockExecutorMap.values()) {
            verify(executor, times(1)).shutdown();
            verify(executor, times(1)).awaitTermination(60, TimeUnit.SECONDS);
        }
    }

    @ParameterizedTest
    @EnumSource(RequestDto.Type.class)
    void testShutdownShouldHandleInterruptedException(RequestDto.Type type) throws InterruptedException {
        ExecutorService mockExecutor = mockExecutorMap.get(type);
        doThrow(new InterruptedException("Interrupted")).when(mockExecutor).awaitTermination(60, TimeUnit.SECONDS);

        requestService.shutdown();

        verify(mockExecutor, times(1)).shutdownNow();
        verify(mockExecutor, times(1)).awaitTermination(60, TimeUnit.SECONDS);
    }

}