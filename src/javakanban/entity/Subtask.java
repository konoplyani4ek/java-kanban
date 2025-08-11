package javakanban.entity;

public class Subtask extends Task {

    private final long epicId;

    public Subtask(String name, String description, long epicId) {
        super(name, description, Type.Subtask);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, long id, Status status, long epicId) { // для апдейта
        super(name, description, id, status, Type.Subtask);
        this.epicId = epicId;
    }

    public Subtask(Long id, Type type, String name, Status status, String description, Long epicId) {
        super(id, type, name, status, description);
        this.epicId = epicId;
    }
    public long getEpicId() {
        return epicId;
    }
//
//    @Override
//    public String toString() {
//        return super.toString() + "," +
//                epicId;
//    }
}