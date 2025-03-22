import javaKanban.entity.Status;
import javaKanban.entity.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SubTaskTest {

    @Test
    void equals_ReturnTrue_SameId() {
        Subtask subtask1 = new Subtask("task1", "disc1", 3, Status.NEW, 2);
        Subtask subtask2 = new Subtask("task2", "disc2", 3, Status.IN_PROGRESS, 2);
        assertEquals(subtask1, subtask2);
    }
}
