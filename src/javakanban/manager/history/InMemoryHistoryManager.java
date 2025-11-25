package javakanban.manager.history;

import javakanban.entity.Node;
import javakanban.entity.Task;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final TaskLinkedList historyList; // final потому что не будем перезаписывать ссылку на другой объект или null, но внутри можно изменить

    @Override
    public void add(Task task) {
        // Удалим старую запись, если она есть
        if (historyList.getNodeMap().containsKey(task.getId())) {
            historyList.removeNode(historyList.getNodeMap().get(task.getId()));
        }
        historyList.linkLast(task);
    }

    @Override
    public void remove(long id) {
        Node node = historyList.getNodeMap().get(id);
        if (node != null) {
            historyList.removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyList.getTasks();
    }

    public InMemoryHistoryManager() {
        this.historyList = new TaskLinkedList();
    }


}


