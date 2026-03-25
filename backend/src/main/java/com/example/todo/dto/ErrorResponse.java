package com.example.todo.dto;

import java.time.LocalDateTime;

public record ErrorResponse(int status, String error, LocalDateTime timestamp) {
}