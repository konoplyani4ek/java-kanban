package manager;

import javakanban.manager.history.HistoryManager;
import javakanban.manager.Managers;
import javakanban.manager.history.InMemoryHistoryManager;
import javakanban.manager.task.TaskManager;
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
