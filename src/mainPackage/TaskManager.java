package mainPackage;

import mainPackage.entity.Epic;
import mainPackage.entity.Status;
import mainPackage.entity.Subtask;
import mainPackage.entity.Task;

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
            epic.setStatus(Status.NEW);
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
        Subtask subtaskById = null;
        for (Subtask subtask : subtaskHashMap.values()){
            if (subtask.getId() == id){
                subtaskById = subtask;
            }
        }
        return subtaskById;
    }

    public Epic getEpicBySubtaskId(long id) {
        Epic epicById = null;
        for (Epic epic : epicHashMap.values()) {
            if (epic.getId() == id){
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
        subtask.setId(generateNewId());
        subtaskHashMap.put(subtask.getId(), subtask);
        Epic epic = taskManager.getEpicBySubtaskId(subtask.getEpicId());
        epic.addSubtaskId(subtask.getId());
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
        Epic epic = taskManager.getEpicBySubtaskId(subtask.getEpicId());
        taskManager.updateEpicStatus(epic);


    }

    public void updateEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
    }

    public void deleteTaskById(long id) {
        taskHashMap.remove(id);
    }

    public void deleteSubtaskById(long id) {
        subtaskHashMap.remove(id);
    }

    public void deleteEpicById(long id) { // можно рациональнее?
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