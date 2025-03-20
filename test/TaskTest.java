import javaKanban.entity.Status;
import javaKanban.entity.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void shouldBeEqualsWhenCompareById(){
        Task task1 = new Task("name1", "disc1", 5, Status.NEW);
        Task task2 = new Task("name2", "disc2", 5, Status.DONE);
        assertEquals(task1, task2);
    }
}