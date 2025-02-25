package ru.task.slovo.server.comand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.task.slovo.server.AppServer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class StopCommandTest {
    private AppServer mockServer;
    private StopCommand stopCommand;

    @BeforeEach
    void setUp() {
        mockServer = Mockito.mock(AppServer.class);
        stopCommand = new StopCommand(mockServer);
    }

    @Test
    void testExecute() throws Exception {
        stopCommand.execute();

        verify(mockServer, times(1)).stop();
    }
}