package ru.task.slovo;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.task.slovo.server.comand.CommandManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ApplicationConsoleListenerTest {

    @Test
    void testStartConsoleCommandListener() throws Exception {
        CommandManager mockCommandManager = Mockito.mock(CommandManager.class);

        String input = "test\n";
        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        System.setIn(inputStream);

        Method method = Application.class.getDeclaredMethod("startConsoleCommandListener", CommandManager.class);
        method.setAccessible(true);

        method.invoke(null, mockCommandManager);

        Thread.sleep(100);

        verify(mockCommandManager, times(1)).executeCommand("test");
    }
}
