package javakanban.manager.task;

import javakanban.entity.*;
import javakanban.manager.CsvConverter;
import javakanban.manager.ManagerSaveException;

import java.io.*;

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
    public Task putNewTask(Task task) {
        taskHashMap.put(task.getId(), task);
        save();
        return task;
    }

    @Override
    public Subtask putNewSubtask(Subtask subtask) {
        Epic epic = epicHashMap.get(subtask.getEpicId());
        if (epic == null) {
            throw new RuntimeException("Такой Subtask добавить нельзя ");
        } else {
            subtaskHashMap.put(subtask.getId(), subtask);
            epic.addSubtaskId(subtask.getId());
        }
        save();
        return subtask;
    }

    @Override
    public Epic putNewEpic(Epic epic) {
        epicHashMap.put(epic.getId(), epic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Long id, Task task) {
        super.updateTask(id, task);
        save();
        return task;
    }

    @Override
    public Subtask updateSubtask(Long id, Subtask subtask) {
        super.updateSubtask(id, subtask);
        save();
        return subtask;
    }

    @Override
    public Epic updateEpic(Long id, Epic epic) {
        super.updateEpic(id, epic);
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


    private void save() {
        try (Writer writer = new FileWriter(file)) {

            for (Task task : getAllTasks()) {
                writer.write(CsvConverter.toStringCSV(task));
            }
            for (Epic epic : getAllEpics()) {
                writer.write(CsvConverter.toStringCSV(epic));
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(CsvConverter.toStringCSV(subtask));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("ОШИБКА: данные не сохранены в файл");
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
                    Task task = CsvConverter.fromString(string);

                    switch (task.getTaskType()) {
                        case EPIC:
                            fileBackedTaskManager.putNewEpic((Epic) task);
                            break;
                        case SUBTASK:
                            fileBackedTaskManager.putNewSubtask((Subtask) task);
                            break;
                        case TASK:
                            fileBackedTaskManager.putNewTask(task);
                            break;
                        default:
                            throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getTaskType());
                    }
                }
            }

        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", exception);
        }

        return fileBackedTaskManager;
    }


}
