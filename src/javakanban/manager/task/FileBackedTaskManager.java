package javakanban.manager.task;

import javakanban.entity.*;
import javakanban.manager.history.HistoryManager;
import javakanban.manager.history.InMemoryHistoryManager;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(File file) { // не понимаю какой конструктор нужен для метода  loadFromFile
        super(InMemoryHistoryManager);
        this.file = file;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public ArrayList<Subtask> getAllSubtasks() {
        return super.getAllSubtasks();
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return super.getAllEpics();
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
            System.out.println("ОШИБКА: данные не сохранены в файл");
        }
    }

    private String toString(Task task) {
        if (task instanceof Epic) {
            Epic epic = (Epic) task;
            return epic.getId() + ",EPIC,"
                    + epic.getName() + ","
                    + epic.getStatus() + ","
                    + epic.getDescription() + "\n";
        } else if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return subtask.getId() + ",SUBTASK,"
                    + subtask.getName() + ","
                    + subtask.getStatus() + ","
                    + subtask.getDescription() + ","
                    + subtask.getEpicId() + "\n";
        } else {
            return task.getId() + ",TASK,"
                    + task.getName() + ","
                    + task.getStatus() + ","
                    + task.getDescription() + "\n";
        }
    }


    private Task fromString(String value) {
        String[] elements = value.split(",");
        Long id = Long.parseLong(elements[0]);
        String typeString = elements[1];
        Type type = Type.valueOf(elements[1]);
        String name = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];

        switch (typeString) {
            case "TASK":
                return new Task(id, type, name, status, description);
            case "EPIC":
                return new Epic(id, type, name, status, description);
            case "SUBTASK":
                Long epicId = Long.parseLong(elements[5]);
                return new Subtask(id, type, name, status, description, epicId);
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) { // не понимаю, нужны методы inMemoryHistoryManager
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                Task task = manager.fromString(line);

                if (task.getClass() == Task.class) {
                    historyManager.
                }

                if (task instanceof Epic) {

                }

                if (task instanceof SubTask) {

                }

            }
        } catch (IOException exception) {
            throw new RuntimeException("ОШИБКА: данные не сохранены из файла", exception);
        }
        return manager;
    }
}
