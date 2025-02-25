package ru.task.slovo.server.comand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.server.AppServer;

public class StartCommand implements Command {
    private final Logger logger = LogManager.getLogger(StartCommand.class);
    private final AppServer server;

    public StartCommand(AppServer server) {
        this.server = server;
    }

    @Override
    public void execute() {

        if (server.isStopped()) {
            try {
                server.start();
                logger.info("Сервер запущен.");
            } catch (Exception e) {
                logger.error("Ошибка при запуске сервера: {}", e.getMessage());
            }
        } else {
            logger.warn("Сервер запущен уже запущен!");
        }
    }
}