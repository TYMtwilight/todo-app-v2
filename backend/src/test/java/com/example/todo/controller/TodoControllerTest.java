package com.example.todo.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.hamcrest.Matchers.hasSize;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.service.TodoService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TodoController.class)
public class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        reset(todoService);
    }

    @SuppressWarnings("null")
    @Test
    void GET_全TODO取得_200を返す() throws Exception {
        TodoResponse active = new TodoResponse(1L, "テスト1", false, LocalDateTime.now(), LocalDateTime.now());
        TodoResponse completed = new TodoResponse(2L, "テスト2", true, LocalDateTime.now(), LocalDateTime.now());
        // Given
        List<TodoResponse> all = List.of(active, completed);
        List<TodoResponse> activeList = List.of(active);
        List<TodoResponse> completedList = List.of(completed);
        when(todoService.findAll("ALL")).thenReturn(all);
        when(todoService.findAll("ACTIVE")).thenReturn(activeList);
        when(todoService.findAll("COMPLETED")).thenReturn(completedList);

        // When & Then
        mockMvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("テスト1"));
        mockMvc.perform(get("/api/todos").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("テスト1"));
        mockMvc.perform(get("/api/todos").param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("テスト2"));

    }

    @SuppressWarnings("null")
    @Test
    void POST_正常なリクエストで201を返す() throws Exception {
        // Given
        TodoRequest request = new TodoRequest("新しい TODO");

        TodoResponse response = new TodoResponse(
                1L, "新しい TODO", false, LocalDateTime.now(), LocalDateTime.now());
        when(todoService.create(any(TodoRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(
                post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("新しい TODO"));

    }

    @SuppressWarnings("null")
    @Test
    void PUT_正常なリクエストで200を返す() throws Exception {
        // Given
        Long todoId = 1L;
        TodoRequest request = new TodoRequest("更新された TODO", true);

        TodoResponse response = new TodoResponse(
                todoId, "更新された TODO", true, LocalDateTime.now(), LocalDateTime.now());
        when(todoService.update(any(Long.class), any(TodoRequest.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(
                put("/api/todos/{id}", todoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新された TODO"))
                .andExpect(jsonPath("$.completed").value(true));

    }

    @Test
    void PATCH_正常なリクエストで200を返す() throws Exception {
        // Given
        Long todoId = 1L;

        TodoResponse response = new TodoResponse(
                todoId, "トグルされた TODO", true, LocalDateTime.now(), LocalDateTime.now());
        when(todoService.toggleComplete(todoId)).thenReturn(response);

        // When & Then
        mockMvc.perform(
                patch("/api/todos/{id}", todoId)

        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("トグルされた TODO"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void DELETE_存在するTODOを削除_204を返す() throws Exception {
        // Given: 存在する ID で削除すると正常に削除される
        Long existingId = 1L;
        doNothing().when(todoService).delete(eq(existingId));

        // When & Then: DELETE /api/todos/1 にリクエスト
        mockMvc.perform(delete("/api/todos/{id}", existingId))
                // Then: 204 No Content を返す
                .andExpect(status().isNoContent());
    }

    @Test
    void GET_データが存在しない状態でゲット_404を返す() throws Exception {
        // Given
        when(todoService.findAll("ALL")).thenThrow(new TodoNotFoundException("ALL"));
        when(todoService.findAll("ACTIVE")).thenThrow(new TodoNotFoundException("ACTIVE"));
        when(todoService.findAll("COMPLETED")).thenThrow(new TodoNotFoundException("COMPLETED"));

        // When & Then
        mockMvc.perform(get("/api/todos").param("status", "ALL"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/todos").param("status", "ACTIVE"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/todos").param("status", "COMPLETED"))
                .andExpect(status().isNotFound());
    }

    @SuppressWarnings("null")
    @Test
    void POST_タイトル空でバリデーションエラー_400を返す() throws Exception {
        // Given
        TodoRequest request = new TodoRequest("");

        // When & Then
        mockMvc.perform(post("/api/todos").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @SuppressWarnings("null")
    @Test
    void PUT_タイトル空でバリデーションエラー_400を返す() throws Exception {
        // Given
        Long todoId = 1L;
        // Given
        TodoRequest request = new TodoRequest("");

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", todoId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @SuppressWarnings("null")
    @Test
    void PUT_存在しないIDで更新_404を返す() throws Exception {
        // Given
        Long todoId = 999L;
        TodoRequest request = new TodoRequest("テスト");
        when(todoService.update(eq(todoId), any(TodoRequest.class)))
                .thenThrow(new TodoNotFoundException(todoId));

        // When & Then
        mockMvc.perform(put("/api/todos/{id}", todoId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void PATCH_存在しないTODOでトグル_404を返す() throws Exception {
        // Given
        when(todoService.toggleComplete(999L)).thenThrow(new TodoNotFoundException(999L));

        // When & Then
        mockMvc.perform(patch("/api/todos/{id}/toggle", 999L))
                .andExpect(status().isNotFound());
    }

    @SuppressWarnings("null")
    @Test
    void DELETE_存在しないTODOを削除_404を返す() throws Exception {
        // Given: 存在しない ID で削除しようとすると TodoNotFoundException を投げる
        Long notFoundId = 999L;
        doThrow(new TodoNotFoundException(notFoundId))
                .when(todoService).delete(eq(notFoundId));

        // When & Then: DELETE /api/todos/999 にリクエスト
        mockMvc.perform(delete("/api/todos/{id}", notFoundId))
                // Then: 404 Not Found を返す
                .andExpect(status().isNotFound());
    }
}
