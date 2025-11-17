package entity;

import javakanban.entity.Status;
import javakanban.entity.Subtask;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SubTaskTest {

    @Test
    void equals_ReturnTrue_SameId() {
        Subtask subtask1 = new Subtask(3L, "task1", Status.NEW, "disc1",  Duration.ofMinutes(60), LocalDateTime.now(), 2L);
        Subtask subtask2 = new Subtask(3L, "task2", Status.NEW, "disc2",  Duration.ofMinutes(120), LocalDateTime.now(), 2L);
        assertEquals(subtask1.getId(), subtask2.getId());
        assertEquals(subtask1, subtask2);
    }
}
