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
        manager.putNewTask(new Task(1L, TaskType.TASK, "написать код", Status.IN_PROGRESS, "методы ФЗ"));
        manager.putNewEpic(new Epic(2L, TaskType.EPIC, "написать тесты", Status.IN_PROGRESS, "тест менеджера"));
        manager.putNewSubtask(new Subtask(3L, TaskType.SUBTASK, "тест 1", Status.NEW, "тест save", 2L));

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

        manager.putNewTask(new Task(1L, TaskType.TASK, "написать тесты", Status.IN_PROGRESS, "тест менеджера"));
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

        manager.putNewTask(new Task(1L, TaskType.TASK, "написать тесты", Status.IN_PROGRESS, "тест менеджера"));
        manager.putNewTask(new Task(2L, TaskType.TASK, "проверить code-style", Status.IN_PROGRESS, "исправить форматирование"));

        manager.deleteTaskById(1);

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        assertFalse(newManager.getAllTasks().contains(1), "Задача  1 не удалена");
        assertNotNull(newManager.getTaskById(2));

        Task task = newManager.getTaskById(2);
        assertEquals(TaskType.TASK, task.getTaskType());
        assertEquals("проверить code-style", task.getName());
        assertEquals(Status.IN_PROGRESS, task.getStatus());
        assertEquals("исправить форматирование", task.getDescription());
    }

    @Test
    void putThreeTasks_AllTasksAdded() throws IOException {
        File file = new File(tempDir, "tasks.csv");
        try (FileWriter writer = new FileWriter(file)) {
        } // для создания пустого файла
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        manager.putNewTask(new Task(1L, TaskType.TASK, "написать тесты", Status.IN_PROGRESS, "тест менеджера"));
        manager.putNewTask(new Task(2L, TaskType.TASK, "проверить code-style", Status.IN_PROGRESS, "исправить форматирование"));

        FileBackedTaskManager newManager = FileBackedTaskManager.loadFromFile(file);
        newManager.putNewTask(new Task(3L, TaskType.TASK, "исправить ошибки", Status.IN_PROGRESS, "неверная логика"));


        assertEquals(3, newManager.getAllTasks().size());
    }
}
