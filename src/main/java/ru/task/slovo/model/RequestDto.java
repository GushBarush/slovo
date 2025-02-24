package ru.task.slovo.model;

public class RequestDto {
    public enum Type { A, B }

    private final Type type;
    private final int x;

    public RequestDto(Type type, int x) {
        this.type = type;
        this.x = x;
    }

    public Type getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    @Override
    public String toString() {
        return "Request{" + "type=" + type + ", x=" + x + '}';
    }
}