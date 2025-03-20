import javaKanban.HistoryManager;
import javaKanban.InMemoryHistoryManager;
import javaKanban.Managers;
import javaKanban.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    void shouldReturnManagers(){
        HistoryManager historyManager = Managers.getDefaultHistory();
        TaskManager taskManager = Managers.getDefault();
        Assertions.assertNotNull(historyManager);
        Assertions.assertNotNull(taskManager);
    }
}
