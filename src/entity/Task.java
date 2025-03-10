package entity;

import java.util.Objects;

public class Task {

    private String name;
    private String description;
    private long id;
    private Status status;

    @Override
    public boolean equals(Object object) { // в наследниках такой же
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id;
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

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    public Task(String name, String description, long id, Status status){ // мб нужен для тестов  метода обновлния
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}