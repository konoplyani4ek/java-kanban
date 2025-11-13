package javakanban.manager.task;

import javakanban.entity.Epic;
import javakanban.entity.Status;
import javakanban.entity.Subtask;
import javakanban.entity.Task;
import javakanban.manager.Managers;
import javakanban.manager.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


public class InMemoryTaskManager implements TaskManager {

    protected long taskIdCounter = 1;

    protected final HashMap<Long, Task> taskHashMap = new HashMap<>();
    // final потому что не будем перезаписывать ссылку на другой объект или null, но внутри можно изменить
    protected final HashMap<Long, Subtask> subtaskHashMap = new HashMap<>();
    protected final HashMap<Long, Epic> epicHashMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime).thenComparing(Task::getId));

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
        epicHashMap.values().forEach(epic -> {
            epic.removeSubtasks();
            updateEpicFields(epic);
        });
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
        return task;
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
        checkIfIntersects(task);
        task.setId(generateNewId());
        taskHashMap.put(task.getId(), task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Subtask putNewSubtask(Subtask subtask) {
        checkIfIntersects(subtask);
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic == null) {
            throw new RuntimeException("Такой Subtask добавить нельзя ");
        } else {
            subtask.setId(generateNewId());
            subtaskHashMap.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
            updateEpicFields(epic);
            addToPrioritized(subtask);
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
    public Task updateTask(Task task) {
        checkIfIntersects(task);
        Long id = task.getId();
        if (!taskHashMap.containsKey(id)) {
            throw new IllegalArgumentException("Task with id " + id + " not found.");
        }
        removeFromPrioritized(taskHashMap.get(task.getId()));
        taskHashMap.put(id, task);
        addToPrioritized(task);
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Long id = subtask.getId();
        if (!subtaskHashMap.containsKey(id)) {
            throw new IllegalArgumentException("Subtask with id " + id + " not found.");
        }
        subtaskHashMap.put(id, subtask);
        Epic epic = getEpicById(subtask.getEpicId());
        updateEpicFields(epic);
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Long id = epic.getId();
        if (!epicHashMap.containsKey(id)) {
            throw new IllegalArgumentException("Epic with id " + id + " not found.");
        }
        epicHashMap.put(id, epic);
        return epic;
    }

    @Override
    public void deleteTaskById(long id) {
        Task task = taskHashMap.get(id);
        taskHashMap.remove(id);
        removeFromPrioritized(task);
    }

    @Override
    public void deleteSubtaskById(long id) {
        Subtask subtaskToDelete = subtaskHashMap.get(id);
        subtaskHashMap.remove(id);
        Epic epicToUpdate = getEpicById(subtaskToDelete.getEpicId());
        epicToUpdate.getSubtasksId().remove(id);
        updateEpicFields(epicToUpdate);
        removeFromPrioritized(subtaskToDelete);
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
    public List<Subtask> getSubtasksByEpic(Epic epic) {
        return epic.getSubtasksId().stream()
                .map(subtaskHashMap::get)
                .filter(Objects::nonNull)
                .toList(); //немодиф список чтобы никто не изменил
    }

    private void updateEpicFields(Epic epic) { // метод внутреннего пользования
        if (epic == null) {
            throw new NullPointerException("Epic is null");
        }
        List<Subtask> subtasks = getSubtasksByEpic(epic);

        if (epic.getSubtasksId() == null || epic.getSubtasksId().isEmpty()) {

            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        //временные параметры
        epic.setDuration(getEpicDuration(subtasks));
        epic.setStartTime(getEpicStartTime(subtasks));
        epic.setEndTime(getEpicEndTime(subtasks));

        //статус
        List<Status> statuses = epic.getSubtasksId().stream()
                .map(subtaskHashMap::get)
                .filter(Objects::nonNull)
                .map(Subtask::getStatus)
                .toList();

        if (statuses.isEmpty()) {
            epic.setStatus(Status.NEW);
        } else if (statuses.stream().allMatch(status -> status == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (statuses.stream().allMatch(status -> status == Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private LocalDateTime getEpicStartTime(List<Subtask> subtasks) {
        return subtasks.stream()
                .filter(Objects::nonNull)
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .sorted()
                .findFirst()
                .orElse(null);
    }

    private Duration getEpicDuration(List<Subtask> subtasks) {
        return subtasks.stream()
                .filter(Objects::nonNull)
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
    }

    private LocalDateTime getEpicEndTime(List<Subtask> subtasks) {
        return subtasks.stream()
                .filter(Objects::nonNull)
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    @Override
    public List<Task> getHistory() {
        System.out.println("Ваша история поиска");
        return historyManager.getHistory();
    }

    private boolean isIntersects(Task t1, Task t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        if (t1.getStartTime() == null || t2.getStartTime() == null || t1.getEndTime() == null || t2.getEndTime() == null) {
            return false;
        }
        if (t1.getId().equals(t2.getId())) {
            return false;
        }
        return t1.getStartTime().isBefore(t2.getEndTime())
                && t2.getStartTime().isBefore(t1.getEndTime());
    }

    private boolean isIntersects(Task newTask) {
        if (newTask == null || newTask.getStartTime() == null || newTask.getDuration() == null) return false;
        return getPrioritizedTasks().stream()
                .filter(task -> task.getStartTime() != null && task.getDuration() != null)
                .filter(task -> !Objects.equals(task.getId(), newTask.getId()))
                .anyMatch(task -> isIntersects(task, newTask));
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private void addToPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    private void removeFromPrioritized(Task task) {
        if (task != null && task.getStartTime() != null) {
            prioritizedTasks.remove(task);
        }
    }

    private void checkIfIntersects(Task task) {
        if (isIntersects(task)) {
            throw new IllegalStateException("Нельзя добавить. Задача" + task.getName() + " пересекается с другой по времени");
        }
    }
}