package javaKanban;

import javaKanban.entity.Epic;
import javaKanban.entity.Status;
import javaKanban.entity.Subtask;
import javaKanban.entity.Task;

import java.util.ArrayList;
import java.util.HashMap;


public class InMemoryTaskManager implements TaskManager {

    private long taskIdCounter = 1;

    private HashMap<Long, Task> taskHashMap;
    private HashMap<Long, Subtask> subtaskHashMap;
    private HashMap<Long, Epic> epicHashMap;

    private HistoryManager historyManager = Managers.getDefaultHistory(); // почему интерфейсу вызываем метод, где будет вызван класс, а не Класс = Класс
    private static InMemoryTaskManager inMemoryTaskManager; // чтобы содержался только один объект

    private InMemoryTaskManager() { // приватный конструктор для синглтон
        this.taskHashMap = new HashMap<>();
        this.subtaskHashMap = new HashMap<>();
        this.epicHashMap = new HashMap<>();
    }

    static InMemoryTaskManager getInstance() { // вызвать один раз в мейне для создания экземпляра
        if (InMemoryTaskManager.inMemoryTaskManager == null) {
            InMemoryTaskManager.inMemoryTaskManager = new InMemoryTaskManager();
        }
        return InMemoryTaskManager.inMemoryTaskManager;
    }

    @Override
    public long generateNewId() {
        return taskIdCounter++;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(taskHashMap.values());
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtaskHashMap.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epicHashMap.values());
    }

    @Override
    public void clearAllTasks() {
        taskHashMap.clear();
    }

    @Override
    public void clearAllSubtasks() {
        for (Epic epic : epicHashMap.values()) {
            epic.removeSubtasks();
            updateEpicStatus(epic);
        }
        subtaskHashMap.clear();
    }

    @Override
    public void clearAllEpics() {
        subtaskHashMap.clear();
        epicHashMap.clear();
    }

    @Override
    public Task getTaskById(long id) {  // в мейн классе некорректно отрабатывает, появляются лишние записи в истории
        historyManager.add(taskHashMap.get(id));
        return taskHashMap.get(id);
    }

    @Override
    public Subtask getSubtaskById(long id) {
        historyManager.add(subtaskHashMap.get(id));
        return subtaskHashMap.get(id);
    }

    @Override
    public Epic getEpicById(long id) {
        historyManager.add(epicHashMap.get(id));
        return epicHashMap.get(id);
    }


    @Override
    public void putNewTask(Task task) {
        task.setId(generateNewId());
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void putNewSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        if (epic == null) {
            System.out.println("\nЭпика с  номером " + subtask.getEpicId() + " не существует");
        } else {
            subtask.setId(generateNewId());
            subtaskHashMap.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
    }

    @Override
    public void putNewEpic(Epic epic) {
        epic.setId(generateNewId());
        epicHashMap.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        taskHashMap.put(task.getId(), task);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        subtaskHashMap.put(subtask.getId(), subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        updateEpicStatus(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
    }

    @Override
    public void deleteTaskById(long id) {
        taskHashMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(long id) {
        Subtask subtaskToDelete = subtaskHashMap.get(id);
        subtaskHashMap.remove(id);
        Epic epicToUpdate = getEpicById(subtaskToDelete.getEpicId());
        epicToUpdate.getSubtasksId().remove(id);
        updateEpicStatus(epicToUpdate);

    }

    @Override
    public void deleteEpicById(long id) {
        ArrayList<Long> subtasksToDelete = epicHashMap.get(id).getSubtasksId();
        for (Long subtaskId : subtasksToDelete) {
            subtaskHashMap.remove(subtaskId);
        }
        epicHashMap.remove(id);
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        ArrayList<Subtask> subtasksById = new ArrayList<>();
        for (Long id : epic.getSubtasksId()) {
            subtasksById.add(subtaskHashMap.get(id));
        }
        return subtasksById;
    }

    @Override
    public void updateEpicStatus(Epic epic) { // метод внутреннего пользования
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

    public ArrayList<Task> getHistory() {
        System.out.println("Ваша история поиска");
        return historyManager.getHistory();
    }

}