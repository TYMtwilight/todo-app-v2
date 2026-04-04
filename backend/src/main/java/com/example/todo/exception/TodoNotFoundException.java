package com.example.todo.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(Long id) {
        super("TODO が見つかりません: id=" + id);
    }

    public TodoNotFoundException(String status) {
        super("TODO が見つかりません： status=" + status);
    }
}
