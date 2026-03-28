package com.example.todo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TodoRequest(
        @NotBlank(message = "タイトルは必須です") @Size(max = 100, message = "タイトルは100文字以内です") String title,
        Boolean completed) {
}
