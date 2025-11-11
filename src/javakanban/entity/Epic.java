package javakanban.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Long> subtasksId = new ArrayList<>();
    private LocalDateTime endTime; // логика подсчета в таск менеджере

    public ArrayList<Long> getSubtasksId() {
        return subtasksId;
    }

    public void removeSubtasks() {
        subtasksId.clear();
    }

    public void addSubtaskId(Long id) {
        subtasksId.add(id);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }
    // новых конструкторов пока нет

    public Epic(String name, String description) {
        super(name, description);
        this.setDuration(null);
        this.setStartTime(null);
        this.endTime = null;
    } // новые поля null

    public Epic(Long id, String name, Status status, String description, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
        super(id, name, status, description, duration, startTime);
        this.endTime = endTime;
    } // этот конструктор нужен для inMemoryTaskManager и апдейта, новые поля учтены

    @Override
    public String toString() {
        return "Epic {" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", taskType=" + TaskType.EPIC +
                ", duration=" + getDuration() +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                '}';
    }

}