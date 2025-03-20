package javaKanban;

public class Managers {

    public static TaskManager getDefault() {
        return InMemoryTaskManager.getInstance();
    }

    public static HistoryManager getDefaultHistory() { // почему тип один
        return InMemoryHistoryManager.getInstance(); // а возвращаем  объект, который имплементирует интерфейс
    }
}
