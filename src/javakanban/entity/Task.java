package javakanban.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {

    private final String name; // final потому что не меняется напрямую, при создании Таска
    private final String description;
    private Long id; // есть setId, Таск создается без него
    private Status status;
    private Duration duration; // при сохранении сделать минутами, при загрузке - Duration
    private LocalDateTime startTime;

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

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    // конструктор с новыми параметрами не нужен
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    } // базовый старый конструктор

    public Task(Long id, String name, Status status, String description, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    } // этот конструктор нужен для FileBackTaskManager и апдейт, новые поля учтены

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + TaskType.TASK +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}



