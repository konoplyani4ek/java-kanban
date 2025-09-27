package manager.task;

import javakanban.entity.*;
import javakanban.manager.CsvConverter;
import javakanban.manager.task.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @TempDir
    File tempDir;

    @Test
    void loadFromFile_shouldLoadTasksCorrectly() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        try (FileWriter writer = new FileWriter(file)) {
        } // для создания пустого файла
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);
        manager.putNewTask(new Task("написать код", "методы ФЗ"));
        manager.putNewEpic(new Epic("написать тесты", "тест менеджера"));
        manager.putNewSubtask(new Subtask("тест 1", "тест save", 2L));

        assertNotNull(manager);

        Task task = manager.getTaskById(1);
        assertNotNull(task);
        assertEquals(TaskType.TASK, task.getTaskType());
        assertEquals("написать код", task.getName());

        Epic epic = manager.getEpicById(2);
        assertNotNull(epic);
        assertEquals("написать тесты", epic.getName());
        assertTrue(epic.getSubtasksId().contains(3L));

        Subtask subtask = manager.getSubtaskById(3);
        assertNotNull(subtask);
        assertEquals(2L, subtask.getEpicId());
        assertEquals("тест 1", subtask.getName());
    }

    @Test
    void putNewTask_FileNotEmpty() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        try (FileWriter writer = new FileWriter(file)) {
        } // для создания пустого файла
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        manager.putNewTask(new Task("написать тесты", "тест менеджера"));
        assertNotNull(manager.getTaskById(1));

        Path filePath = file.toPath();
        assertFalse(Files.size(filePath) == 0, "Файл пустой!");
    }

    @Test
    void deleteTaskById_FileUpdated() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        try (FileWriter writer = new FileWriter(file)) {
        } // для создания пустого файла
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        manager.putNewTask(new Task("написать тесты", "тест менеджера"));
        manager.putNewTask(new Task("проверить code-style", "исправить форматирование"));

        manager.deleteTaskById(1);

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        assertFalse(newManager.getAllTasks().contains(1), "Задача  1 не удалена");
        assertNotNull(newManager.getTaskById(2));

        Task task = newManager.getTaskById(2);
        assertEquals(TaskType.TASK, task.getTaskType());
        assertEquals("проверить code-style", task.getName());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals("исправить форматирование", task.getDescription());
    }

    @Test
    void putThreeTasks_ThreeTasksAdded() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        try (FileWriter writer = new FileWriter(file)) {
        } // для создания пустого файла
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        manager.putNewTask(new Task("написать тесты", "тест менеджера"));
        manager.putNewTask(new Task("проверить code-style", "исправить форматирование"));

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        newManager.putNewTask(new Task("исправить ошибки", "неверная логика"));

        assertEquals(3, newManager.getAllTasks().size());
    }

    @Test
    void putNewEpicAndSubtask_EpicHasSubtaskId() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        try (FileWriter writer = new FileWriter(file)) {
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        Epic epic = new Epic("написать тесты", "тест менеджера");
        assertNull(epic.getId());
        manager.putNewEpic(epic);
        assertEquals(1, epic.getId());

        Subtask subtask = new Subtask("исправить ошибки", "неверная логика", 1L);
        assertNull(subtask.getId());
        manager.putNewSubtask(subtask);
        assertEquals(2, subtask.getId());
        assertTrue(epic.getSubtasksId().contains(2L));
    }
}
