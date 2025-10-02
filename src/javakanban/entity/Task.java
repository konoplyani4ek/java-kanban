package javakanban.entity;

import java.util.Objects;

public class Task {

    private final String name; // final потому что не меняется напрямую, при создании Таска
    private final String description;
    private Long id; // есть setId, Таск создается без него
    private Status status;

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

    public Long getId() {
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

    public TaskType getTaskType() {
        return TaskType.TASK;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(Long id, String name, Status status, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    } // этот конструктор нужен для inMemoryTaskManager и апдейта

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + TaskType.TASK +
                '}';
    }
}



