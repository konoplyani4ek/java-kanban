package javakanban;

import javakanban.entity.*;
import javakanban.manager.history.HistoryManager;
import javakanban.manager.history.InMemoryHistoryManager;
import javakanban.manager.task.InMemoryTaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) { // можно удалять


        HistoryManager historyManager = new InMemoryHistoryManager();
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();

        System.out.println("задачи:");
        Task task1 = new Task("Задача1", "Тема задачи 1");
        Task task2 = new Task("Задача2", "Тема задачи 2");
        inMemoryTaskManager.putNewTask(task1);
        inMemoryTaskManager.putNewTask(task2);
        System.out.println(inMemoryTaskManager.getAllTasks());

        // проверяю вывод истории со своей реализацией листа
        inMemoryTaskManager.getTaskById(1);
        System.out.println(inMemoryTaskManager.getHistory());

        System.out.println("\nэпик с двумя подзадачами:");

        System.out.println("сам эпик");
        Epic epic = new Epic("Эпик1", "Тема эпика 1");
        inMemoryTaskManager.putNewEpic(epic);
        System.out.println(inMemoryTaskManager.getAllEpics());

        System.out.println("созданы 2 подзадачи.");

        Subtask subtask1 = new Subtask("подзадача1", " Тема подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("подзадача2", " Тема подзадачи 2", epic.getId());

        inMemoryTaskManager.putNewSubtask(subtask1);
        inMemoryTaskManager.putNewSubtask(subtask2);

        System.out.println("все эпики (пока один) " + inMemoryTaskManager.getAllEpics());
        System.out.println("все подзадачи " + inMemoryTaskManager.getAllSubtasks());

        inMemoryTaskManager.putNewEpic(new Epic("Эпик2", "Тема эпика 2"));
        System.out.println("\nдобавил второй эпик " + inMemoryTaskManager.getAllEpics());
        inMemoryTaskManager.deleteEpicById(6);

        System.out.println("\nизменить статус задачи1");
        System.out.println("было  " + inMemoryTaskManager.getAllTasks());
        task1.setStatus(Status.IN_PROGRESS);
        System.out.println("стало " + inMemoryTaskManager.getAllTasks());

        System.out.println("\n изменить статус эпика через изменение статуса in Progress:");
        inMemoryTaskManager.updateSubtask(new Subtask(5L, "подзадача 2 обновлена", Status.IN_PROGRESS, "Тема подзадачи 2 обновлена", Duration.ofMinutes(60), LocalDateTime
                .now(), 3L));

        System.out.println(inMemoryTaskManager.getAllEpics());
        System.out.println(inMemoryTaskManager.getAllSubtasks());

        System.out.println("\n изменить статус эпика через изменение статуса подзадач Done:");
        inMemoryTaskManager.updateSubtask(new Subtask(4L, "подзадача 2 обновлена", Status.IN_PROGRESS, "Тема подзадачи 2 обновлена", Duration.ofMinutes(60), LocalDateTime
                .now(), 3L));
        inMemoryTaskManager.updateSubtask(new Subtask(5L, "подзадача 2 обновлена", Status.IN_PROGRESS, "Тема подзадачи 2 обновлена", Duration.ofMinutes(60), LocalDateTime
                .now(), 3L));
        System.out.println(inMemoryTaskManager.getAllSubtasks());
        System.out.println(inMemoryTaskManager.getAllEpics());

        System.out.println("\n удалить задачу");
        System.out.println("было " + inMemoryTaskManager.getAllTasks());
        inMemoryTaskManager.deleteTaskById(1);
        System.out.println("стало " + inMemoryTaskManager.getAllTasks());

        inMemoryTaskManager.putNewSubtask(new Subtask("подзадача 3", "тема 3", epic.getId()));
        System.out.println("\n добавил сабтаск id=7 \n" + inMemoryTaskManager.getAllSubtasks());
        System.out.println("\n статус эпика тоже изменился \n " + inMemoryTaskManager.getAllEpics());
        inMemoryTaskManager.deleteSubtaskById(7);
        System.out.println("\n удалил его \n " + inMemoryTaskManager.getAllSubtasks());

        System.out.println("\n удалить эпик");
        System.out.println("было " + inMemoryTaskManager.getAllEpics());
        inMemoryTaskManager.deleteEpicById(3);
        System.out.println("стало  " + inMemoryTaskManager.getAllEpics());

        System.out.println("\n пробую создать задачу с несуществующим epicId");
        inMemoryTaskManager.putNewSubtask(new Subtask("подзадача 3", "тема 3", 9L));


    }
}