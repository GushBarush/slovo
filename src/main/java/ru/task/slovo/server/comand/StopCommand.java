package ru.task.slovo.server.comand;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.task.slovo.server.AppServer;

public class StopCommand implements Command {
    private final Logger logger = LogManager.getLogger(StartCommand.class);
    private final AppServer server;

    public StopCommand(AppServer server) {
        this.server = server;
    }

    @Override
    public void execute() {
        if (server != null && !server.isStopped()) {
            try {
                server.stop();
                logger.info("Сервер остановлен.");
            } catch (Exception e) {
                logger.error("Ошибка при остановке сервера: ", e);
            }
        }
    }
}