import javaKanban.entity.Status;
import javaKanban.entity.Subtask;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class SubTaskTest {


    @Test
    void shouldBeEqualsWhenCompareById() {
        Subtask subtask1 = new Subtask("task1", "disc1", 3, Status.NEW, 2);
        Subtask subtask2 = new Subtask("task2", "disc2", 3, Status.IN_PROGRESS, 2);
        assertEquals(subtask1, subtask2);
    }

    @Test
    void shouldNotBeAddedToItself() {//проверьте, что объект Subtask нельзя сделать своим же эпиком;
    }                                   // не понимаю как сделать. через определенные конструкторы это же можно сделать
}
