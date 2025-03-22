package javaKanban.manager.task;

import javaKanban.entity.Epic;
import javaKanban.entity.Subtask;
import javaKanban.entity.Task;

import java.util.ArrayList;

public interface TaskManager {

    ArrayList<Task> getAllTasks();

    ArrayList<Subtask> getAllSubtasks();

    ArrayList<Epic> getAllEpics();

    void clearAllTasks();

    void clearAllSubtasks();

    void clearAllEpics();

    Task getTaskById(long id);

    Subtask getSubtaskById(long id);

    Epic getEpicById(long id);

    void putNewTask(Task task);

    void putNewSubtask(Subtask subtask);

    void putNewEpic(Epic epic);

    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);

    void deleteTaskById(long id);

    void deleteSubtaskById(long id);

    void deleteEpicById(long id);

    ArrayList<Subtask> getSubtasksByEpic(Epic epic);

    ArrayList<Task> getHistory();
}
