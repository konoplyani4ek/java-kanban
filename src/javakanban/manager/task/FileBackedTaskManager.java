package javakanban.manager.task;

import javakanban.entity.*;
import javakanban.manager.ManagerSaveException;


import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public void clearAllTasks() {
        super.clearAllTasks();
        save();
    }

    @Override
    public void clearAllSubtasks() {
        super.clearAllSubtasks();
        save();
    }

    @Override
    public void clearAllEpics() {
        super.clearAllEpics();
        save();
    }

    @Override
    public Task getTaskById(long id) {
        return super.getTaskById(id);
    }

    @Override
    public Subtask getSubtaskById(long id) {
        return super.getSubtaskById(id);
    }

    @Override
    public Epic getEpicById(long id) {
        return super.getEpicById(id);
    }

    @Override
    public Task putNewTask(Task task) {
        super.putNewTask(task);
        save();
        return task;
    }

    @Override
    public Subtask putNewSubtask(Subtask subtask) {
        super.putNewSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic putNewEpic(Epic epic) {
        super.putNewEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(long id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public ArrayList<Subtask> getSubtasksByEpic(Epic epic) {
        return super.getSubtasksByEpic(epic);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }

    private void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(task.toString());
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epic.toString());
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtask.toString());
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("ОШИБКА: данные не сохранены в файл");
        }
    }

    private String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getName(),
                task.getStatus(),
                task.getDescription()
        );
    }

    public Task fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Пустая или null строка не может быть распарсена");
        }
        String[] elements = value.split(",");
        Long id = Long.parseLong(elements[0]);
        TaskType taskType = TaskType.valueOf(elements[1]);
        String name = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];


        switch (taskType) {
            case TASK:
                return new Task(id, taskType, name, status, description);
            case EPIC:
                return new Epic(id, taskType, name, status, description);
            case SUBTASK:
                Long epicId = Long.parseLong(elements[5]);
                return new Subtask(id, taskType, name, status, description, epicId);
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        if (file == null) {
            System.out.println("Файл автосохранения не был передан!");
            return null;
        }

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String string = bufferedReader.readLine().trim();

                if (!string.isEmpty()) {
                    Task task = fileBackedTaskManager.fromString(string);

                    if (task instanceof Epic) {
                        fileBackedTaskManager.putNewEpic((Epic) task);
                    } else if (task instanceof Subtask) {
                        fileBackedTaskManager.putNewSubtask((Subtask) task);
                    } else {
                        fileBackedTaskManager.putNewTask(task);
                    }
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", exception);
        }

        return fileBackedTaskManager;
    }
}
