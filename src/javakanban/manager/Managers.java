package javakanban.manager;

import javakanban.manager.history.HistoryManager;
import javakanban.manager.history.InMemoryHistoryManager;
import javakanban.manager.task.InMemoryTaskManager;
import javakanban.manager.task.TaskManager;

public class Managers {

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }

    public static HistoryManager getHistoryManager() {
        return new InMemoryHistoryManager();
    }
}
