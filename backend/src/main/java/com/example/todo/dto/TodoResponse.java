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

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
