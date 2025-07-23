package javaKanban.manager.history;

import javaKanban.entity.Node;
import javaKanban.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    // убираю лист тут и меняю на кастомный лист
    private final TaskLinkedList historyList; // final потому что не будем перезаписывать ссылку на другой объект или null, но внутри можно изменить
    // private static final int HISTORY_LIST_MAX_SIZE = 10;


    @Override
    public void add(Task task) {
        // Удалим старую запись, если она есть
        if (historyList.nodeMap.containsKey(task.getId())) {
            historyList.removeNode(historyList.nodeMap.get(task.getId()));
        }
        historyList.linkLast(task);
    }

    @Override
    public void remove(long id) {
        Node node = historyList.nodeMap.get(id);
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

class TaskLinkedList {

    Map<Long, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;
    private int size = 0;

    // Добавляет задачу в конец списка
    public void linkLast(Task task) {
        Node newNode = new Node(tail, task, null);
        if (tail != null) {
            tail.next = newNode;
        } else {
            head = newNode; // если список был пустой
        }
        tail = newNode;
        size++;
        nodeMap.put(task.getId(), newNode);
    }

    // Возвращает все задачи в виде списка
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }

    public void removeNode(Node node) {
        if (node.prev != null) { // если есть ПЕРЕД
            node.prev.next = node.next; // то СЛЕДУЮЩИЙ у ПЕРЕД будет тот что после НОДЫ
        } else { // если ПЕРЕД нет, значит нода = HEAD
            head = node.next; // берем СЛЕДУЮЩИЙ у ноды
        }
        if (node.next != null) { // если есть СЛЕДУЮЩИЙ
            node.next.prev = node.prev;
        } // то ПЕРЕД у СЛЕДУЮЩЕГО будет ПЕРЕД от НОДЫ
        else { // ЕСЛИ СЛЕДУЮЩЕГО нет, значит нода = TAIL
            tail = node.prev; // берем ПЕРЕД у ноды
        }
        nodeMap.remove(node.task.getId());
        size--;
    }
}