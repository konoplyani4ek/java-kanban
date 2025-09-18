package javakanban.entity;

public class Subtask extends Task {

    private final long epicId;

    public Subtask(String name, String description, long epicId) {
        super(name, description, TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, long id, Status status, long epicId) { // для апдейта
        super(name, description, id, status, TaskType.SUBTASK);
        this.epicId = epicId;
    }

    public Subtask(Long id, TaskType taskType, String name, Status status, String description, Long epicId) {
        super(id, taskType, name, status, description);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

}