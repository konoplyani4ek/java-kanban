import javaKanban.entity.Epic;
import javaKanban.entity.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

public class EpicTest {

    @Test
    void shouldBeEqualsWhenCompareById() {
        Epic epic1 = new Epic("text1", "disc1", 2, Status.NEW);
        Epic epic2 = new Epic("text2", "disc2", 2, Status.IN_PROGRESS);
        Assertions.assertEquals(epic1, epic2);
    }

    @Test
    void shouldNotBeAddedToItself() { // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
        Epic epic = new Epic("text1", "тема disc1 2"); // не понимаю как сделать. через определенные конструкторы это же можно сделать
        epic.addSubtaskId(epic.getId());
    }


}
