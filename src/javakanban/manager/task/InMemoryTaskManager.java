package javakanban.manager.task;

import javakanban.entity.Epic;
import javakanban.entity.Status;
import javakanban.entity.Subtask;
import javakanban.entity.Task;
import javakanban.manager.Managers;
import javakanban.manager.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private long taskIdCounter = 1;

    protected final HashMap<Long, Task> taskHashMap = new HashMap<>();
    // final потому что не будем перезаписывать ссылку на другой объект или null, но внутри можно изменить
    protected final HashMap<Long, Subtask> subtaskHashMap = new HashMap<>();
    protected final HashMap<Long, Epic> epicHashMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    private long generateNewId() {
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
    public Task getTaskById(long id) {
        Task task = taskHashMap.get(id);// чтобы два раза не читать map.get(id), создается объект
        historyManager.add(task);
        return task; // и возвращается
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = subtaskHashMap.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = epicHashMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Task putNewTask(Task task) {
        task.setId(generateNewId());
        taskHashMap.put(task.getId(), task);
        return task;
    }

    @Override
    public Subtask putNewSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic == null) {
            throw new RuntimeException("Такой Subtask добавить нельзя ");
        } else {
            subtask.setId(generateNewId());
            subtaskHashMap.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            updateEpicStatus(epic);
        }
        return subtask;
    }

    @Override
    public Epic putNewEpic(Epic epic) {
        epic.setId(generateNewId());
        epicHashMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Task updateTask(Long id, Task task) {
        if (!taskHashMap.containsKey(id)) {
            throw new IllegalArgumentException("Task with id " + id + " not found.");
        }
        task.setId(id);
        taskHashMap.put(id, task);
        return task;
    }

    @Override
    public Subtask updateSubtask(Long id, Subtask subtask) {
        if (!subtaskHashMap.containsKey(id)) {
            throw new IllegalArgumentException("Subtask with id " + id + " not found.");
        }
        subtask.setId(id);
        subtaskHashMap.put(id, subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Epic updateEpic(Long id, Epic epic) {
        if (!epicHashMap.containsKey(id)) {
            throw new IllegalArgumentException("Epic with id " + id + " not found.");
        }
        epic.setId(id);
        epicHashMap.put(id, epic);
        return epic;
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

    @Override
    public List<Task> getHistory() {
        System.out.println("Ваша история поиска");
        return historyManager.getHistory();
    }

}