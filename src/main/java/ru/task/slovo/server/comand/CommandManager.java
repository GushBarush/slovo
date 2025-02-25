package ru.task.slovo.server.comand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Logger logger = LogManager.getLogger();
    private final Map<String, Command> commands = new HashMap<>();

    public void registerCommand(String name, Command command) {
        commands.put(name, command);
    }

    public void executeCommand(String name) {
        Command command = commands.get(name);
        if (command != null) {
            command.execute();
        } else {
            logger.warn("Неизвестная команда: {}", name);
        }
    }
}