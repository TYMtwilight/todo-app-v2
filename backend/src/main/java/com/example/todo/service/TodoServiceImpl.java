package com.example.todo.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.lang.NonNull;
import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.Todo;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;

@Service
@Transactional(readOnly = true)
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;

    public TodoServiceImpl(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @Override
    public List<TodoResponse> findAll(String status) {
        List<Todo> todos = switch (status.toUpperCase()) {
            case "ACTIVE" -> {
                List<Todo> active = todoRepository.findByCompleted(false);
                if (active.isEmpty()) {
                    throw new TodoNotFoundException("ACTIVE");
                }
                yield active;
            }
            case "COMPLETED" -> {
                List<Todo> completed = todoRepository.findByCompleted(true);
                if (completed.isEmpty()) {
                    throw new TodoNotFoundException("COMPLETED");
                }
                yield completed;
            }
            default -> {
                List<Todo> all = todoRepository.findAllByOrderByCreatedAtDesc();
                if (all.isEmpty()) {
                    throw new TodoNotFoundException("ALL");
                }
                yield all;
            }

        };

        return todos.stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TodoResponse create(TodoRequest request) {
        Todo todo = new Todo();
        todo.setTitle(request.title());
        return TodoResponse.from(todoRepository.save(todo));
    }

    @Override
    @Transactional
    public TodoResponse update(@NonNull Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        todo.setTitle(request.title());
        if (request.completed() != null) {
            todo.setCompleted(request.completed());
        }
        return TodoResponse.from(todoRepository.save(todo));
    }

    @Override
    @Transactional
    public TodoResponse toggleComplete(@NonNull Long id) {
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
        todo.setCompleted(!todo.getCompleted());
        return TodoResponse.from(todoRepository.save(todo));
    }

    @Override
    @Transactional
    public void delete(@NonNull Long id) {
        if (!todoRepository.existsById(id)) {
            throw new TodoNotFoundException(id);
        }
        todoRepository.deleteById(id);
    }
}
