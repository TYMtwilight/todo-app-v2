package com.example.todo.controller;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.todo.dto.ErrorResponse;
import com.example.todo.exception.TodoNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorResponse> handleValidateion(
                        MethodArgumentNotValidException ex) {
                String message = ex.getBindingResult().getFieldErrors().stream()
                                .map(FieldError::getDefaultMessage)
                                .collect(Collectors.joining(","));
                return ResponseEntity.badRequest().body(
                                new ErrorResponse(400, "Bad Request", message, LocalDateTime.now()));
        }

        @ExceptionHandler(TodoNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleNotFound(TodoNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                                new ErrorResponse(404, "Not Found", ex.getMessage(), LocalDateTime.now()));
        }

}
