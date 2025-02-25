package ru.task.slovo.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import ru.task.slovo.servlet.RequestServlet;

public class JettyServer implements AppServer {

    private final Server server;

    public JettyServer(int port) {
        this.server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath("/api-v1");
        context.addServlet(RequestServlet.class, "/request");
        server.setHandler(context);
    }

    @Override
    public void start() throws Exception {
        server.start();
    }

    @Override
    public void stop() throws Exception {
        server.stop();
    }

    @Override
    public void join() throws Exception {
        server.join();
    }

    @Override
    public boolean isStopped() {
        return server.isStopped();
    }
}