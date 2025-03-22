import javaKanban.manager.history.HistoryManager;
import javaKanban.manager.Managers;
import javaKanban.manager.task.TaskManager;
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
