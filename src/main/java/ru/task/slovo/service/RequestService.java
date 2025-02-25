package ru.task.slovo.service;

import ru.task.slovo.model.RequestDto;

public interface RequestService {

    void submit(RequestDto request);

    void shutdown();
}