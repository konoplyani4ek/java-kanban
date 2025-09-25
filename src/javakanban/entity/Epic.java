package javakanban.entity;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Long> subtasksId = new ArrayList<>();

    public ArrayList<Long> getSubtasksId() {
        return subtasksId;
    }

    public void removeSubtasks() {
        subtasksId.clear();
    }

    public void addSubtaskId(Long id) {
        subtasksId.add(id);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public Epic(String name, String description) {
        super(name, description);
        this.setStatus(Status.NEW);
    }

    public Epic(String name, String description, Long id, Status status) {
        super(name, description);
        this.setId(id);
        this.setStatus(status);
    } // для апдейта

    public Epic(Long id, TaskType taskType, String name, Status status, String description) {
        super(id, taskType, name, status, description);
    } // этот конструктор нужен для inMemoryTaskManager

    @Override
    public String toString() {
        return "Epic {" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", taskType=" + getTaskType() +
                '}';
    }

}