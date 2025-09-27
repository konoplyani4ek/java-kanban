package entity;

import javakanban.entity.Status;
import javakanban.entity.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void getStatus_New_WhenCreated() { // поверить статус
        Task task1 = new Task("name1", "disc1");
        assertEquals(task1.getStatus(), Status.NEW);

    }
}