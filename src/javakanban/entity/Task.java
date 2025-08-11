package javakanban.entity;

import java.util.Objects;

public class Task {

    private String name; // почему тогда и тут предлагает сделать final? ведь она тоже может быть изменена через методы?
    private String description;
    private Long id; // а тут final не предлагает, хотя мы подразумеваем что id не меняется?
    private Status status;
    private Type type;

    public Task() {

    }


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

    public Task(String name, String description, Type type) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.type = type;

    }

    public Task(String name, String description, long id, Status status, Type type) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = type;

    }

    public Task(Long id, Type type, String name, Status status, String description) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.type = type;

    }

    @Override
    public String toString() {
        return id + "," +
                type + "," +
                name + "," +
                status + "," +
                description;
    }

    public static Task fromString(String value) {
        String[] parts = value.split(",");
        Task task = new Task();
        task.id = Long.parseLong(parts[0]);
        task.type = Type.valueOf(parts[1]);
        task.name = parts[2];
        task.status = Status.valueOf(parts[3]);
        task.description = parts[4];
        return task;
    }


}