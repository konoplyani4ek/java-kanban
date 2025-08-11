package javakanban.entity;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Long> subtasksId;

    public ArrayList<Long> getSubtasksId() {
        return subtasksId;
    }

    public void removeSubtasks() {
        subtasksId.clear();
    }

    public void addSubtaskId(long id) {
        subtasksId.add(id);
    }

    public Epic(String name, String description) {
        super(name, description, Type.Epic);
        this.subtasksId = new ArrayList<>();

    }

    public Epic(String name, String description, long id, Status status) {
        super(name, description, id, status, Type.Epic);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(Long id, Type type, String name, Status status, String description) {
        super(id, type, name, status, description);
    }

//    @Override
//    public String toString() {
//        return super.toString();
//    }
}