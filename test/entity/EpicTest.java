package entity;

import javakanban.entity.Epic;
import javakanban.entity.Status;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class EpicTest {

    @Test
    void equals_returnTrue_SameId() {
        Epic epic1 = new Epic("text1", "disc1", 2, Status.NEW);
        Epic epic2 = new Epic("text2", "disc2", 2, Status.IN_PROGRESS);
        assertEquals(epic1.getId(), epic2.getId());
        assertEquals(epic1, epic2);
    }



}
