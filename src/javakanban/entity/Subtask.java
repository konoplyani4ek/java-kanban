package javakanban.entity;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final Long epicId;
    //конструктор с новыми полями не нужен
    public Subtask(String name, String description, Long epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(Long id, String name, Status status, String description, Duration duration, LocalDateTime startTime, Long epicId){
        super(id, name, status, description, duration, startTime);
        this.epicId = epicId;
    } // этот конструктор нужен для FileBackedTaskManager и апдейта, новые поля null
    public Long getEpicId() {
        return epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask {" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", taskType=" + TaskType.SUBTASK +
                ", epicId=" + getEpicId() +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                '}';
    }
}