package manager;

import javaKanban.manager.history.HistoryManager;
import javaKanban.manager.Managers;
import javaKanban.manager.history.InMemoryHistoryManager;
import javaKanban.manager.task.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ManagersTest {

    @Test
    void shouldReturnManagers(){
        HistoryManager historyManager = new InMemoryHistoryManager();
        TaskManager taskManager = Managers.getDefault(historyManager);
        Assertions.assertNotNull(historyManager);
        Assertions.assertNotNull(taskManager);
    }
}
