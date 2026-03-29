package com.example.todo.service;

import java.util.List;

import org.springframework.lang.NonNull;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;

public interface TodoService {
    List<TodoResponse> findAll(String status);

    TodoResponse create(TodoRequest todoRequest);

    TodoResponse update(@NonNull Long id, TodoRequest todoRequest);

    TodoResponse toggleComplete(@NonNull Long id);

    void delete(@NonNull Long id);
}
