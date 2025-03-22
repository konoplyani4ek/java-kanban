package javaKanban.manager.history;

import javaKanban.entity.Task;

import java.util.ArrayList;

public interface HistoryManager {

    void add(Task task);
    ArrayList<Task> getHistory();
}
