package javakanban.entity;

public class Subtask extends Task {

    private final Long epicId;

    public Subtask(String name, String description, Long epicId) {
        super(name, description);
        this.setStatus(Status.NEW);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Long id, Status status, Long epicId) {
        super(name, description);
        this.setId(id);
        this.setStatus(status);
        this.epicId = epicId;
    } // для апдейта

    public Subtask(Long id, TaskType taskType, String name, Status status, String description, Long epicId) {
        super(id, taskType, name, status, description);
        this.epicId = epicId;
    } // этот конструктор нужен для inMemoryTaskManager

    public long getEpicId() {
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
                ", taskType=" + getTaskType() +
                ", epicId=" + getEpicId() +
                '}';
    }

}