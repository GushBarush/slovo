package ru.task.slovo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import ru.task.slovo.servlet.RequestServlet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Application {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PORT = 7777;
    private static final String STOP_COMMAND = "stop";

    public static void main(String[] args) throws Exception {
        Server server = new Server(PORT);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/api-v1");
        server.setHandler(context);

        context.addServlet(RequestServlet.class, "/request");

        try {
            server.start();
            startShutdownDemon(server);
            LOGGER.info("Jetty сервер запущен на порту {}", PORT);
            LOGGER.info("Введите '{}' для остановки сервера.", STOP_COMMAND);
            server.join();
        } finally {
            server.destroy();
            LOGGER.info("Сервер остановлен.");
        }
    }

    /**
     * Создаёт поток демон для управления сервером из консоли.
     */
    private static void startShutdownDemon(Server server) {
        Thread consoleThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (STOP_COMMAND.equalsIgnoreCase(line.trim())) {
                        LOGGER.info("Остановка сервера...");
                        try {
                            server.stop();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    } else {
                        LOGGER.error("Неизвестная команда. Для остановки введите '{}'.", STOP_COMMAND);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        consoleThread.setDaemon(true);
        consoleThread.start();
    }
}