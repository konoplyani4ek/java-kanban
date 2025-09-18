package manager.task;

import javakanban.entity.*;
import javakanban.manager.task.FileBackedTaskManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    @TempDir
    File tempDir;

    private final FileBackedTaskManager manager = new FileBackedTaskManager(null);


    @Test
    void fromString_shouldReturnTask_whenTypeIsTask() {
        String line = "1,TASK,Сходить в магазин,NEW,Купить молоко";

        Task task = manager.fromString(line);

        assertNotNull(task);
        assertTrue(task instanceof Task);
        assertEquals(1L, task.getId());
        assertEquals(TaskType.TASK, task.getType());
        assertEquals("Сходить в магазин", task.getName());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals("Купить молоко", task.getDescription());
    }

    //этот тест не работает. неправильно парсит и я не могу найти ошибку java.lang.NumberFormatException: For input string: "и"
    @Test
    void loadFromFile_shouldLoadTasksCorrectly() throws IOException {
        // Создаём временный файл
        File file = new File(tempDir, "tasks.csv");

        //Пишем тестовые данные
        try (FileWriter writer = new FileWriter(file)) {
            writer.write("1,TASK,Сходить в магазин,NEW,Купить молоко\n");
            writer.write("2,EPIC,Переезд,IN_PROGRESS,Собрать вещи\n");
            writer.write("3,SUBTASK,Упаковать книги,DONE,В коробки,2\n");
        }

        // Загружаем менеджер
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(file);

        assertNotNull(manager);

        // Проверяем, что задачи добавлены
        Task task = manager.getTaskById(1L);
        assertNotNull(task);
        assertEquals(TaskType.TASK, task.getType());
        assertEquals("Сходить в магазин", task.getName());

        Epic epic = manager.getEpicById(2L);
        assertNotNull(epic);
        assertEquals("Переезд", epic.getName());

        Subtask subtask = manager.getSubtaskById(3L);
        assertNotNull(subtask);
        assertEquals(2L, subtask.getEpicId());
        assertEquals("Упаковать книги", subtask.getName());
    }
}
