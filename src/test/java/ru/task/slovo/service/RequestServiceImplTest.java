package ru.task.slovo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import ru.task.slovo.model.RequestDto;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class RequestServiceImplTest {

    private RequestServiceImpl requestService;
    private Map<RequestDto.Type, ThreadPoolExecutor> mockExecutorMap;

    @BeforeEach
    void setUp() {
        requestService = new RequestServiceImpl();
        mockExecutorMap = requestService.getExecutorMap();

        for (RequestDto.Type type : RequestDto.Type.values()) {
            ThreadPoolExecutor mockExecutor = mock(ThreadPoolExecutor.class);
            BlockingQueue<Runnable> mockQueue = mock(BlockingQueue.class);

            when(mockExecutor.getQueue()).thenReturn(mockQueue);
            when(mockQueue.size()).thenReturn(0);

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
        ThreadPoolExecutor mockExecutor = mockExecutorMap.get(type);
        ArgumentCaptor<RequestTask> taskCaptor = ArgumentCaptor.forClass(RequestTask.class);

        requestService.submit(request);

        verify(mockExecutor, times(1)).submit(taskCaptor.capture());
        RequestTask capturedTask = taskCaptor.getValue();
        assertEquals(request, capturedTask.getRequest());
    }

    @Test
    void testShutdownShouldShutdownAllExecutors() throws InterruptedException {
        requestService.shutdown();

        for (ThreadPoolExecutor executor : mockExecutorMap.values()) {
            verify(executor, times(1)).shutdown();
            verify(executor, times(1)).awaitTermination(60, TimeUnit.SECONDS);
        }
    }

}