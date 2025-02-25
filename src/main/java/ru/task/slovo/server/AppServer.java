package ru.task.slovo.server;

public interface AppServer {
    void start() throws Exception;

    void stop() throws Exception;

    void join() throws Exception;

    boolean isStopped();
}