package javaKanban.manager;

import javaKanban.manager.history.HistoryManager;
import javaKanban.manager.history.InMemoryHistoryManager;
import javaKanban.manager.task.InMemoryTaskManager;
import javaKanban.manager.task.TaskManager;

public class Managers {

    public static TaskManager getDefault() {
        return InMemoryTaskManager.getInstance();
    }

//    public static TaskManager getDefault(TaskManager taskManager) { // не понимаю какой конструктор с параметрами нужно сделать
//
//    }

    public static HistoryManager getDefaultHistory() { // возвращаем  объект, который имплементирует интерфейс
        return InMemoryHistoryManager.getInstance(); //  можно заменить на другой менеджер, чтобы без доп действий поменять реализации
    }
}
