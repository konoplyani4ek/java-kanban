package entity;

import javakanban.entity.Status;
import javakanban.entity.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SubTaskTest {

    @Test
    void equals_ReturnTrue_SameId() {
        Subtask subtask1 = new Subtask("task1", "disc1", 3L, Status.NEW, 2L);
        Subtask subtask2 = new Subtask("task2", "disc2", 3L, Status.IN_PROGRESS, 2L);
        assertEquals(subtask1.getId(), subtask2.getId());
        assertEquals(subtask1, subtask2);
    }
}
