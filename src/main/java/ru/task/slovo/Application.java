package ru.task.slovo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.server.AppServer;
import ru.task.slovo.server.JettyServer;
import ru.task.slovo.server.comand.CommandManager;
import ru.task.slovo.server.comand.StartCommand;
import ru.task.slovo.server.comand.StopCommand;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PORT = 7777;
    private static final String START_COMMAND = "start";
    private static final String STOP_COMMAND = "stop";

    public static void main(String[] args) {
        AppServer server = new JettyServer(PORT);
        CommandManager commandManager = new CommandManager();

        commandManager.registerCommand(START_COMMAND, new StartCommand(server));
        commandManager.registerCommand(STOP_COMMAND, new StopCommand(server));

        try {
            commandManager.executeCommand(START_COMMAND);
            startConsoleCommandListener(commandManager);

            LOGGER.info("Сервер запущен на порту {}", PORT);
            LOGGER.info("Доступные команды: {}, {}", STOP_COMMAND, STOP_COMMAND);

            server.join();
        } catch (Exception e) {
            LOGGER.error("Ошибка при запуске сервера: ", e);
        } finally {
            commandManager.executeCommand(STOP_COMMAND);
        }
    }

    /**
     * Создает поток демон для прослушивания консольных команд.
     */
    private static void startConsoleCommandListener(CommandManager commandManager) {
        Thread consoleThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    commandManager.executeCommand(line.trim());
                }
            } catch (Exception e) {
                LOGGER.error("Ошибка при чтении консольного ввода: ", e);
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }
}