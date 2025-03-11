package javaKanban;

import javaKanban.entity.Epic;
import javaKanban.entity.Status;
import javaKanban.entity.Subtask;
import javaKanban.entity.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = TaskManager.getInstance();

        System.out.println("задачи:");
        Task task1 = new Task("Задача1", "Тема задачи 1");
        Task task2 = new Task("Задача2", "Тема задачи 2");
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);
        System.out.println(taskManager.getAllTasks());

        System.out.println("\nэпик с двумя подзадачами:");

        System.out.println("сам эпик");
        Epic epic = new Epic("Эпик1", "Тема эпика 1");
        taskManager.putNewEpic(epic);
        System.out.println(taskManager.getAllEpics());
        System.out.println("созданы 2 подзадачи.");
        Subtask subtask1 = new Subtask("подзадача1", " Тема подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("подзадача2", " Тема подзадачи 2", epic.getId());

        taskManager.putNewSubtask(subtask1);
        taskManager.putNewSubtask(subtask2);

        System.out.println("все эпики (пока один) " + taskManager.getAllEpics());
        System.out.println("все подзадачи " + taskManager.getAllSubtasks());

        taskManager.putNewEpic(new Epic("Эпик2", "Тема эпика 2"));
        System.out.println("\nдобавил второй эпик " + taskManager.getAllEpics());
        taskManager.deleteEpicById(6);

        System.out.println("\nизменить статус задачи1");
        System.out.println("было  " + taskManager.getAllTasks());
        task1.setStatus(Status.IN_PROGRESS);
        System.out.println("стало " + taskManager.getAllTasks());

        System.out.println("\n изменить статус эпика через изменение статуса in Progress:");
        taskManager.updateSubtask(new Subtask("подзадача 2 обновлена", "Тема подзадачи 2 обновлена", 5, Status.IN_PROGRESS, 3));


        //taskManager.updateEpicStatus(epic);
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllSubtasks());

        System.out.println("\n изменить статус эпика через изменение статуса подзадач Done:");
        taskManager.updateSubtask(new Subtask("подзадача 1 обновлена", "Тема подзадачи 1 обновлена", 4, Status.DONE, 3));
        taskManager.updateSubtask(new Subtask("подзадача 2 обновлена", "Тема подзадачи 2 обновлена", 5, Status.DONE, 3));
        System.out.println(taskManager.getAllSubtasks());
        System.out.println(taskManager.getAllEpics());

        System.out.println("\n удалить задачу");
        System.out.println("было " + taskManager.getAllTasks());
        taskManager.deleteTaskById(1);
        System.out.println("стало " + taskManager.getAllTasks());

        System.out.println("\n удалить эпик");
        System.out.println("было " + taskManager.getAllEpics());
        taskManager.deleteEpicById(3);
        System.out.println("стало  " + taskManager.getAllEpics());

        taskManager.putNewSubtask(new Subtask("подзадача 3", "тема 3", 9));
    }
}