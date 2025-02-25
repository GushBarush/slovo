package ru.task.slovo.server.comand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.task.slovo.server.AppServer;

import static org.mockito.Mockito.*;

class StartCommandTest {
    private AppServer mockServer;
    private StartCommand startCommand;

    @BeforeEach
    void setUp() {
        mockServer = Mockito.mock(AppServer.class);
        startCommand = new StartCommand(mockServer);
    }

    @Test
    void testExecute() throws Exception {
        when(mockServer.isStopped()).thenReturn(true);

        startCommand.execute();

        verify(mockServer, times(1)).start();
    }

    @Test
    void testExecuteWhenServerIsAlreadyRunning() throws Exception {
        when(mockServer.isStopped()).thenReturn(false);

        startCommand.execute();

        verify(mockServer, never()).start();
    }
}