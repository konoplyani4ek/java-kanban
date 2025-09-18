package javakanban.entity;

public class Subtask extends Task {

    private final long epicId;

    public Subtask(String name, String description, long epicId) {
        super(name, description);
        this.setTaskType(TaskType.SUBTASK);
        this.setStatus(Status.NEW);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, long id, Status status, long epicId) {
        super(name, description);
        this.setId(id);
        this.setStatus(status);
        this.setTaskType(TaskType.SUBTASK);
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
        return "Subtask{" +
                "epicId=" + epicId +
                '}';
    }
    @Override
    public String toStringCSV() {
        return String.format("%d,%s,%s,%s,%s,%s",
                getId(),
                getTaskType(),
                getName(),
                getStatus(),
                getDescription(),
                getEpicId());

    }
}