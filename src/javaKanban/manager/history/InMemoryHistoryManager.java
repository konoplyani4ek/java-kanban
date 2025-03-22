package javaKanban.manager.history;

import javaKanban.entity.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {
    private final ArrayList<Task> historyList; // final потому что не будем перезаписывать ссылку на другой объект или null, но внутри можно изменить
    private static final int HISTORY_LIST_MAX_SIZE = 10;
    @Override
    public void add(Task task) {
        historyList.add(task);
        if (historyList.size() > HISTORY_LIST_MAX_SIZE) {
            historyList.removeFirst();
        }
    }
    @Override
    public ArrayList<Task> getHistory() {
        return historyList;
    }
    private static InMemoryHistoryManager inMemoryHistoryManager;
    private InMemoryHistoryManager(){
        this.historyList = new ArrayList<>();
    }

    public static InMemoryHistoryManager getInstance(){
        if (InMemoryHistoryManager.inMemoryHistoryManager == null) {
            InMemoryHistoryManager.inMemoryHistoryManager = new InMemoryHistoryManager();
        }
        return InMemoryHistoryManager.inMemoryHistoryManager;
    }
}
