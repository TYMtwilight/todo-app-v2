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
            case "ACTIVE" -> todoRepository.findByCompleted(false);
            case "COMPLETED" -> todoRepository.findByCompleted(true);
            default -> todoRepository.findAllByOrderByCreatedAtDesc();
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
