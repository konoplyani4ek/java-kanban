package mainPackage.entity;

public class Subtask extends Task {

    private long epicId;

    public Subtask(String name, String description, long epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, long id, Status status, long epicId) { // для апдейта
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
         return super.toString() +
                " epic Id=" + epicId +
                '}';
    }
}