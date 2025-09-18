package javakanban.entity;

import java.util.Objects;

public class Task {

    private String name; // почему тогда и тут предлагает сделать final? ведь она тоже может быть изменена через методы?
    private String description;
    private Long id; // а тут final не предлагает, хотя мы подразумеваем что id не меняется?
    private Status status;
    private TaskType taskType;

    @Override
    public boolean equals(Object object) { // в наследниках такой же
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    } // в наследниках такой же

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return taskType;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.taskType = TaskType.TASK;
    }

    public Task() {
    }

    public Task(String name, String description, Long id, Status status) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, String description, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.taskType = taskType;
        this.status = Status.NEW;
    } // для создания эпиков и сабтасков

    public Task(String name, String description, long id, Status status, TaskType taskType) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.taskType = taskType;

    }

    public Task(Long id, TaskType taskType, String name, Status status, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.taskType = taskType;

    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s",
                id,
                taskType,
                name,
                status,
                description
        );
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        Task task = new Task();
        task.id = Long.parseLong(parts[0]);
        task.taskType = TaskType.valueOf(parts[1]);
        task.name = parts[2];
        task.status = Status.valueOf(parts[3]);
        task.description = parts[4];
        return task;
    }


}