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
        Task newTask = super.putNewTask(task);
        save();
        return newTask;
    }

    @Override
    public Subtask putNewSubtask(Subtask subtask) {
        Subtask newSubtask = super.putNewSubtask(subtask);
        save();
        return newSubtask;
    }

    @Override
    public Epic putNewEpic(Epic epic) {
        Epic newEpic = super.putNewEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
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
        long maxId = 0;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            while (bufferedReader.ready()) {
                String string = bufferedReader.readLine().trim();

                if (!string.isEmpty()) {
                    Task task = CsvConverter.fromString(string);
                    maxId = Math.max(maxId, task.getId());

                    switch (task.getTaskType()) {
                        case EPIC:
                            fileBackedTaskManager.epicHashMap.put(task.getId(), (Epic) task);
                            break;
                        case SUBTASK:
                            fileBackedTaskManager.subtaskHashMap.put(task.getId(), (Subtask) task);
                            break;
                        case TASK:
                            fileBackedTaskManager.taskHashMap.put(task.getId(), task);
                            break;
                        default:
                            throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getTaskType());
                    }
                }
            }
            for (Subtask subtask : fileBackedTaskManager.subtaskHashMap.values()) {
                Epic epic = fileBackedTaskManager.epicHashMap.get(subtask.getEpicId());
                if (epic != null) {
                    epic.getSubtasksId().add(subtask.getId());
                } else {
                    System.out.println("В файле сабтаск с несуществующим epicId: " + subtask.getEpicId());
                }
            }
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка при загрузке данных из файла", exception);
        }
        fileBackedTaskManager.taskIdCounter = maxId + 1;


        return fileBackedTaskManager;
    }


}
