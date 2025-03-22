import javaKanban.entity.Epic;
import javaKanban.entity.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EpicTest {

    @Test
    void equals_returnTrue_SameId() {
        Epic epic1 = new Epic("text1", "disc1", 2, Status.NEW);
        Epic epic2 = new Epic("text2", "disc2", 2, Status.IN_PROGRESS);
        Assertions.assertEquals(epic1, epic2);
    }



}
