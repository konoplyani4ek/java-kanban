package javakanban.manager.history;

import javakanban.entity.Node;
import javakanban.entity.Task;
import javakanban.entity.TaskLinkedList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    // убираю лист тут и меняю на кастомный лист
    private final TaskLinkedList historyList; // final потому что не будем перезаписывать ссылку на другой объект или null, но внутри можно изменить
    // private static final int HISTORY_LIST_MAX_SIZE = 10;


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
        Node node = historyList.getNodeMap().get(id); // мне кажется уместным сделать Node внутренним классом у TaskLinkedList, но тогда
        if (node != null) {                           // надо переписать этот метод без инициализации ноды, так?
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


