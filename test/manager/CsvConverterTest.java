package manager;

import javakanban.entity.Status;
import javakanban.entity.Task;
import javakanban.entity.TaskType;
import javakanban.manager.CsvConverter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvConverterTest {

    @Test
    void fromString_shouldReturnTask_whenTypeIsTask() {
        String line = "1,TASK,Сходить в магазин,NEW,Купить молоко,60,2025-11-09T03:12:45.123";

        Task task = CsvConverter.fromString(line);

        assertNotNull(task);
        assertInstanceOf(Task.class, task);
        assertEquals(1L, task.getId());
        assertEquals(TaskType.TASK, task.getTaskType());
        assertEquals("Сходить в магазин", task.getName());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals("Купить молоко", task.getDescription());
        assertEquals(60, task.getDuration().toMinutes());
    }
}
