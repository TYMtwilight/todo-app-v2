package com.example.todo.dto;

import java.time.LocalDateTime;

import com.example.todo.entity.Todo;

public class TodoResponse {

    private Long id;
    private String title;
    private Boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public TodoResponse() {
    }

    public TodoResponse(Long id, String title, Boolean completed,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.completed = completed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /** Entity → DTO 変換 */
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt());
    }

    public Long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }


    public Boolean getCompleted() {
        return completed;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
