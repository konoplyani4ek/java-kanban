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
            updateEpicStatus(epic);
            updateEpicTemporalParams(epic);
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
        if (hasIntersection(task)) {
            throw new IllegalStateException("Нельзя добавить. Таск пересекается с другим по времени");
        }
        task.setId(generateNewId());
        taskHashMap.put(task.getId(), task);
        addToPrioritized(task);
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
            updateEpicTemporalParams(epic);
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
        if (hasIntersection(task)) {
            throw new IllegalStateException("Нельзя добавить. Таск пересекается с другим по времени");
        }
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
        updateEpicStatus(epic);
        updateEpicTemporalParams(epic);
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
        taskHashMap.remove(id);
    }

    @Override
    public void deleteSubtaskById(long id) {
        Subtask subtaskToDelete = subtaskHashMap.get(id);
        subtaskHashMap.remove(id);
        Epic epicToUpdate = getEpicById(subtaskToDelete.getEpicId());
        epicToUpdate.getSubtasksId().remove(id);
        updateEpicStatus(epicToUpdate);
        updateEpicTemporalParams(epicToUpdate);

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
        return epic.getSubtasksId().stream()
                .map(subtaskHashMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    private void updateEpicStatus(Epic epic) {
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

    private void updateEpicTemporalParams(Epic epic) { // метод внутреннего пользования
        if (epic == null || epic.getSubtasksId() == null || epic.getSubtasksId().isEmpty()) {

            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }

        Duration epicTotal = Duration.ZERO;
        LocalDateTime earliest = null;
        LocalDateTime latest = null;

        for (Long subtaskId : epic.getSubtasksId()) {
            Subtask subtask = subtaskHashMap.get(subtaskId);
            if (subtask == null) {
                continue;
            }

            Duration duration = subtask.getDuration();
            if (duration != null) {
                epicTotal = epicTotal.plus(duration);
            }


            LocalDateTime startTimeSubtask = subtask.getStartTime();
            if (startTimeSubtask != null) {
                if (earliest == null || startTimeSubtask.isBefore(earliest)) {
                    earliest = startTimeSubtask;
                }
            }

            LocalDateTime endTimeSubtask = subtask.getEndTime();
            if (endTimeSubtask != null) {
                if (latest == null || endTimeSubtask.isAfter(latest)) {
                    latest = endTimeSubtask;
                }
            }
        }

        epic.setDuration(epicTotal);
        epic.setStartTime(earliest);
        epic.setEndTime(latest);
    }


    @Override
    public List<Task> getHistory() {
        System.out.println("Ваша история поиска");
        return historyManager.getHistory();
    }

    private boolean isIntersects(Task t1, Task t2) {
        if (t1 == null || t2 == null) return false;
        if (t1.getStartTime() == null || t2.getStartTime() == null) return false;

        LocalDateTime start1 = t1.getStartTime();
        LocalDateTime end1 = t1.getEndTime() != null ? t1.getEndTime() : start1;
        LocalDateTime start2 = t2.getStartTime();
        LocalDateTime end2 = t2.getEndTime() != null ? t2.getEndTime() : start2;

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    private boolean hasIntersection(Task newTask) {
        if (newTask == null || newTask.getStartTime() == null) return false;

        return getPrioritizedTasks().stream()
                .filter(Objects::nonNull)
                .filter(task -> task.getStartTime() != null)
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
}