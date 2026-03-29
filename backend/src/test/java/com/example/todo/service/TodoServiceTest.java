package com.example.todo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.todo.dto.TodoRequest;
import com.example.todo.dto.TodoResponse;
import com.example.todo.entity.Todo;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.repository.TodoRepository;

@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoServiceImpl todoService;

    // ===== create =====

    @SuppressWarnings("null")
    @Test
    void 新しいTODOを作成できる() {
        // Given
        TodoRequest request = new TodoRequest("テスト TODO", null);

        Todo savedTodo = new Todo();
        savedTodo.setTitle(request.title());
        savedTodo.setCompleted(false);

        when(todoRepository.save(notNull())).thenReturn(savedTodo);

        // When
        TodoResponse response = todoService.create(request);

        // Then
        assertThat(response.getTitle()).isEqualTo("テスト TODO");
        assertThat(response.getCompleted()).isFalse();
        verify(todoRepository, times(1)).save(notNull());
    }

    // ===== findAll =====

    @Test
    void ALLを指定すると全件取得できる() {
        // Given
        Todo todo1 = new Todo();
        todo1.setTitle("TODO 1");
        Todo todo2 = new Todo();
        todo2.setTitle("TODO 2");

        when(todoRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(todo1, todo2));

        // When
        List<TodoResponse> response = todoService.findAll("ALL");

        // Then
        assertThat(response).hasSize(2);
        assertThat(response.get(0).getTitle()).isEqualTo("TODO 1");
    }

    @Test
    void ACTIVEを指定すると未完了のTODOのみ取得できる() {
        // Given
        Todo activeTodo = new Todo();
        activeTodo.setTitle("未完了");
        activeTodo.setCompleted(false);

        when(todoRepository.findByCompleted(false)).thenReturn(List.of(activeTodo));

        // When
        List<TodoResponse> response = todoService.findAll("ACTIVE");

        // Then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getTitle()).isEqualTo("未完了");
        assertThat(response.get(0).getCompleted()).isFalse();
    }

    @Test
    void COMPLETEDを指定すると完了済みのTODOのみ取得できる() {
        // Given
        Todo completedTodo = new Todo();
        completedTodo.setTitle("完了済み");
        completedTodo.setCompleted(true);

        when(todoRepository.findByCompleted(true)).thenReturn(List.of(completedTodo));

        // When
        List<TodoResponse> response = todoService.findAll("COMPLETED");

        // Then
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getTitle()).isEqualTo("完了済み");
        assertThat(response.get(0).getCompleted()).isTrue();
    }

    // ===== update =====
    @SuppressWarnings("null")
    @Test
    void 既存のTODOを更新できる() {
        // Given
        Todo existing = new Todo();
        existing.setId(1L);
        existing.setTitle("変更前");
        existing.setCompleted(false);

        TodoRequest request = new TodoRequest("変更後", false);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(todoRepository.save(notNull())).thenReturn(existing);

        // When
        TodoResponse response = todoService.update(1L, request);

        // Then
        assertThat(response.getTitle()).isEqualTo("変更後");
        assertThat(response.getCompleted()).isFalse();
    }

    @Test
    void 存在しないIDで更新すると例外が発生する() {
        // Given
        when(todoRepository.findById(999L)).thenReturn(Optional.empty());

        TodoRequest request = new TodoRequest("更新", false);

        // When & Then
        assertThatThrownBy(() -> todoService.update(999L, request))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("TODO が見つかりません");
    }

    // ===== toggleComplete =====
    @SuppressWarnings("null")
    @Test
    void 未完了のTODOをトグルすると完了になる() {
        // Given
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("テスト");
        todo.setCompleted(false);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(notNull())).thenReturn(todo);

        // When
        TodoResponse response = todoService.toggleComplete(1L);

        // Then
        assertThat(response.getCompleted()).isTrue();
    }

    @SuppressWarnings("null")
    @Test
    void 完了のTODOをトグルすると未完了になる() {
        // Given
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("テスト");
        todo.setCompleted(true);

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(notNull())).thenReturn(todo);

        // When
        TodoResponse response = todoService.toggleComplete(1L);

        // Then
        assertThat(response.getCompleted()).isFalse();
    }

    // ===== delete =====
    @Test
    void 既存のTODOを削除できる() {
        // Given
        when(todoRepository.existsById(1L)).thenReturn(true);

        // When
        todoService.delete(1L);

        // Then
        verify(todoRepository, times(1)).deleteById(1L);
    }

    @Test
    void 存在しないIDで削除すると例外が発生する() {
        // Given
        when(todoRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> todoService.delete(999L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("TODO が見つかりません");
    }
}
