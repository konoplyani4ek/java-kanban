import entity.Epic;
import entity.Status;
import entity.Subtask;
import entity.Task;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = TaskManager.getInstance();

        System.out.println("задачи:");
        Task task1 = new Task("Задача1", "Тема задачи 1");
        Task task2 = new Task("Задача2", "Тема задачи 2");
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);
        System.out.println(taskManager.getAllTasks());

        System.out.println("\n эпик с двумя подзадачами:");
        System.out.println("сначала 2 подзадачи:");
        Subtask subtask1 = new Subtask("подзадача1", " Тема подзадачи 1", 3);
        Subtask subtask2 = new Subtask("подзадача2", " Тема подзадачи 2", 3);
        taskManager.putNewSubtask(subtask1);
        taskManager.putNewSubtask(subtask2);
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("сам эпик с 2 подзадачами");
        Epic epic = new Epic("Эпик1", "Тема эпика 1", new ArrayList<Long>(Arrays.asList(subtask1.getId(), subtask2.getId())));

        taskManager.putNewEpic(epic);
        System.out.println(taskManager.getAllEpics());

        System.out.println("изменить статус задачи1");
        task1.setStatus(Status.IN_PROGRESS);
        System.out.println(taskManager.getAllTasks());

        System.out.println("\n изменить статус эпика через изменение статуса in Progress:");
        subtask2.setStatus(Status.DONE);
        System.out.println(subtask1);
        System.out.println(subtask2);
        taskManager.updateEpicStatus(epic);
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n изменить статус эпика через изменение статуса подзадач Done:");
        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateEpicStatus(epic);
        System.out.println(subtask1);
        System.out.println(subtask2);
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n удалить задачу");
        System.out.println("было " + taskManager.getAllTasks());
        taskManager.deleteTaskById(1);
        System.out.println("стало " + taskManager.getAllTasks());

        System.out.println("\n удалить эпик");
        System.out.println("было " + taskManager.getAllEpics());
        taskManager.deleteEpicById(5);
        System.out.println("стало " + taskManager.getAllEpics());
    }
}