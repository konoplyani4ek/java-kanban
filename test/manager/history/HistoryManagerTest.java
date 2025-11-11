package manager.history;

import javakanban.entity.Task;
import javakanban.manager.Managers;
import javakanban.manager.history.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HistoryManagerTest {
    Task task;
    HistoryManager historyManager;

    @BeforeEach
    void init() {
        historyManager = Managers.getDefaultHistoryManager();
        task = new Task("Test task", "Test task description");
    }

    @Test
    void add() {
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        Task task2 = new Task("name2", "description 2");
        task2.setId(2);
        historyManager.add(task2);
        assertEquals(2, historyManager.getHistory().size(), "Должно быть две задачи");
    }


    @Test
    void shouldReturnEmptyHistory() {
        final List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "History  should  be empty");
    }

    @Test
    void shouldRemoveTask() {
        task.setId(1);
        historyManager.add(task);

        historyManager.remove(task.getId());

        final List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    void shouldDeleteSameTaskFromHistory() {
        task.setId(1);
        historyManager.add(task);
        historyManager.add(task);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Должна быть только одна задача");
    }

    @Test
    void keepLastViewInHistory() {
        task.setId(1);
        Task task2 = new Task("name 2", "description 2");
        task2.setId(1);
        historyManager.add(task);
        historyManager.add(task2);
        final List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Должна быть она задача");
        assertEquals("name 2", history.getFirst().getName(), "Должна отобразиться вторая задача");
    }
}
