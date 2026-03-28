package com.example.todo.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.example.todo.entity.Todo;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TodoRepositoryTest {

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void 全件取得_作成日時の降順で返される() {
        // Given
        Todo todo1 = new Todo();
        todo1.setTitle("最初のTODO");
        todoRepository.save(todo1);

        Todo todo2 = new Todo();
        todo2.setTitle("2番目のTODO");
        todoRepository.save(todo2);

        // When
        List<Todo> todos = todoRepository.findAllByOrderByCreatedAtDesc();

        // Then
        assertThat(todos).hasSize(2);
        assertThat(todos.get(0).getTitle()).isEqualTo("2番目のTODO");
    }

    @Test
    void 完了状態でフィルタリングできる() {
        // Given
        Todo activeTodo = new Todo();
        activeTodo.setTitle("未完了");
        activeTodo.setCompleted(false);
        todoRepository.save(activeTodo);

        Todo completedTodo = new Todo();
        completedTodo.setTitle("完了");
        completedTodo.setCompleted(true);
        todoRepository.save(completedTodo);

        // When
        List<Todo> activeTodos = todoRepository.findByCompleted(false);

        // Then
        assertThat(activeTodos).hasSize(1);
        assertThat(activeTodos.get(0).getTitle()).isEqualTo("未完了");

        // When
        List<Todo> completedTodos = todoRepository.findByCompleted(true);

        // Then
        assertThat(completedTodos).hasSize(1);
        assertThat(completedTodos.get(0).getTitle()).isEqualTo("完了");
    }
}
