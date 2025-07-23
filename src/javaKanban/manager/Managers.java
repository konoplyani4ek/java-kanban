package javaKanban.manager;

import javaKanban.manager.history.HistoryManager;
import javaKanban.manager.task.InMemoryTaskManager;
import javaKanban.manager.task.TaskManager;

public class Managers {

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }
}
