package ru.task.slovo.server.comand;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CommandManagerTest {
    private CommandManager commandManager;
    private Command mockCommand;

    @BeforeEach
    void setUp() {
        commandManager = new CommandManager();
        mockCommand = Mockito.mock(Command.class);
    }

    @Test
    void testRegisterCommand() {
        commandManager.registerCommand("test", mockCommand);
        commandManager.executeCommand("test");

        verify(mockCommand, times(1)).execute();
    }

    @Test
    void testExecuteUnknownCommand() {
        assertDoesNotThrow(() -> commandManager.executeCommand("unknown"));
    }
}