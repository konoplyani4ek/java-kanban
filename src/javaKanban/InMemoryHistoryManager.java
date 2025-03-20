package javaKanban;

import javaKanban.entity.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager { // где почитать про все эти менеджеры и зачем их делать?
    private ArrayList<Task> historyList; // почему ее предлагает сделать final, если она будет постоянно изменяться?
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

    static InMemoryHistoryManager getInstance(){
        if (InMemoryHistoryManager.inMemoryHistoryManager == null) {
            InMemoryHistoryManager.inMemoryHistoryManager = new InMemoryHistoryManager();
        }
        return InMemoryHistoryManager.inMemoryHistoryManager;
    }
}
