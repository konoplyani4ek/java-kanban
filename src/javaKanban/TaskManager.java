package javaKanban;

import javaKanban.entity.Epic;
import javaKanban.entity.Status;
import javaKanban.entity.Subtask;
import javaKanban.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class TaskManager {

    private long taskIdCounter = 1;

    private HashMap<Long, Task> taskHashMap;
    private HashMap<Long, Subtask> subtaskHashMap;
    private HashMap<Long, Epic> epicHashMap;

    private static TaskManager taskManager; // чтобы содержался только один объект

    public static TaskManager getInstance() { // вызвать один раз в мейне для создания экземпляра
        if (taskManager == null) {
            taskManager = new TaskManager();
        }
        return taskManager;
    }

    private TaskManager() { // приватный конструктор для синглтон
        this.taskHashMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();

    }

    private long generateNewId() {
        return taskIdCounter++;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicHashMap.values());
    }

    public void clearAllTasks() {
        taskHashMap.clear();
    }

    public void clearAllSubtasks() {
        for (Epic epic : epicHashMap.values()) {
            epic.removeSubtasks();
            epic.setStatus(Status.NEW); // не понимаю, что именно тут должно быть?
        }
        subtaskHashMap.clear();
    }

//    заменить метод на такой? см. ст 59
//    public void setEpicStatusNew(Epic epic){
//        epic.setStatus(Status.NEW);
//    }

    public void clearAllEpics() {
        subtaskHashMap.clear();
        epicHashMap.clear();
    }

    public Task getTaskById(long id) {
        return taskHashMap.get(id);
    }

    public Subtask getSubtaskById(long id) {
        return subtaskHashMap.get(id);
    }

    public Epic getEpicBySubtaskId(long id) { // этот метод сущестует для получения эпика по айди эпика, указанному в сабтаске.
        // метод getEpicById возвращает эпик по айди эпика. или я что-то не понял?
        Epic epicById = null;
        for (Epic epic : epicHashMap.values()) {
            if (epic.getId() == id) {
                epicById = epic;
            }
        }
        return epicById;
    }

    public void putNewTask(Task task) {
        task.setId(generateNewId());
        taskHashMap.put(task.getId(), task);
    }

    public void putNewSubtask(Subtask subtask) {
        try {
            subtask.setId(generateNewId());
            subtaskHashMap.put(subtask.getId(), subtask);
            Epic epic = getEpicBySubtaskId(subtask.getEpicId());
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        } catch (NullPointerException e){
            System.out.println("Эпика с таким номером не существует");
        }
    }

    public void putNewEpic(Epic epic) {
        epic.setId(generateNewId());
        epicHashMap.put(epic.getId(), epic);
    }

    public void updateTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    public void updateSubtask(Subtask subtask) {
        subtaskHashMap.put(subtask.getId(), subtask);
        Epic epic = getEpicBySubtaskId(subtask.getEpicId());
        updateEpicStatus(epic); // я не совсем понял, почему именно так работает
    }

    public void updateEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
    }

    public void deleteTaskById(long id) {
        taskHashMap.remove(id);
    }

    public void deleteSubtaskById(long id) {
        subtaskHashMap.remove(id);
        Epic epicToUpdate = getEpicBySubtaskId(id);
        updateEpicStatus(epicToUpdate);
    }

    public void deleteEpicById(long id) {
        ArrayList<Long> subtasksToDelete = epicHashMap.get(id).getSubtasksId();
        for (Long subtaskId : subtasksToDelete) {
            subtaskHashMap.remove(subtaskId);
        }
        epicHashMap.remove(id);
    }

    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksById = new ArrayList<>();
        for (Long id : epic.getSubtasksId()) {
            subtasksById.add(subtaskHashMap.get(id));
        }
        return subtasksById;
    }

    private void updateEpicStatus(Epic epic) { // метод внутреннего пользования
        if (epic.getSubtasksId().isEmpty()) {
            epic.setStatus(Status.NEW);
        } else {
            int counterNew = 0;
            int counterDone = 0;
            for (long id : epic.getSubtasksId()) {
                switch (subtaskHashMap.get(id).getStatus()) {
                    case NEW:
                        counterNew++;
                        break;
                    case DONE:
                        counterDone++;
                        break;
                }
            }
            if (counterNew == epic.getSubtasksId().size()) {
                epic.setStatus(Status.NEW);
            } else if (counterDone == epic.getSubtasksId().size()) {
                epic.setStatus(Status.DONE);
            } else {
                epic.setStatus(Status.IN_PROGRESS);
            }
        }
    }
}