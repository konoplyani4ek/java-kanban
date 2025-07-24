package javakanban.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskLinkedList {

    private final Map<Long, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;

    public Map<Long, Node> getNodeMap() {
        return nodeMap;
    }

    // Добавляет задачу в конец списка
    public void linkLast(Task task) {
        Node newNode = new Node(tail, task, null);
        if (tail != null) {
            tail.setNext(newNode);
        } else {
            head = newNode; // если список был пустой
        }
        tail = newNode;
        nodeMap.put(task.getId(), newNode);
    }

    // Возвращает все задачи в виде списка
    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.getTask());
            current = current.getNext();
        }
        return tasks;
    }

    public void removeNode(Node node) {
        if (node.getPrev() != null) { // если есть ПЕРЕД
            node.getPrev().setNext(node.getNext()); // то СЛЕДУЮЩИЙ у ПЕРЕД будет тот что после НОДЫ
        } else { // если ПЕРЕД нет, значит нода = HEAD
            head = node.getNext(); // берем СЛЕДУЮЩИЙ у ноды
        }
        if (node.getNext()!= null) { // если есть СЛЕДУЮЩИЙ
            node.getNext().setPrev(node.getPrev());
        } // то ПЕРЕД у СЛЕДУЮЩЕГО будет ПЕРЕД от НОДЫ
        else { // ЕСЛИ СЛЕДУЮЩЕГО нет, значит нода = TAIL
            tail = node.getPrev(); // берем ПЕРЕД у ноды
        }
        nodeMap.remove(node.getTask().getId());
    }


}