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
            updateEpicStatus(epic);
        }
        subtaskHashMap.clear();
    }

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

    public Epic getEpicById(long id) {
        return epicHashMap.get(id);
    }

    public void putNewTask(Task task) {
        task.setId(generateNewId());
        taskHashMap.put(task.getId(), task);
    }

    public void putNewSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        if (epic == null) {
            System.out.println("\nЭпика с  номером " + subtask.getEpicId()  + " не существует");
        } else {
            subtask.setId(generateNewId());
            subtaskHashMap.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
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
        Epic epic = getEpicById(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    public void updateEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
    }

    public void deleteTaskById(long id) {
        taskHashMap.remove(id);
    }

    public void deleteSubtaskById(long id) {
        Subtask subtaskToDelete = subtaskHashMap.get(id);
        subtaskHashMap.remove(id);
        Epic epicToUpdate = getEpicById(subtaskToDelete.getEpicId());
        epicToUpdate.getSubtasksId().remove(id);
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