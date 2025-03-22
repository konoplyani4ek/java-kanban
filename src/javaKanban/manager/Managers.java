package javaKanban.manager;

import javaKanban.manager.history.HistoryManager;
import javaKanban.manager.history.InMemoryHistoryManager;
import javaKanban.manager.task.InMemoryTaskManager;
import javaKanban.manager.task.TaskManager;

public class Managers {

    public static TaskManager getDefault(HistoryManager historyManager) {
        return new InMemoryTaskManager(historyManager);
    }


//    public static HistoryManager getDefaultHistory() { // возвращаем  объект, который имплементирует интерфейс
//        return InMemoryHistoryManager.getInstance(); //  можно заменить на другой менеджер, чтобы без доп действий поменять реализации
//    }
}
