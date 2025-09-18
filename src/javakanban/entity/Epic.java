package javakanban.entity;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Long> subtasksId = new ArrayList<>();

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
        super(name, description, TaskType.EPIC);
        //this.subtasksId = new ArrayList<>();

    }

    public Epic(String name, String description, long id, Status status) {
        super(name, description, id, status, TaskType.EPIC);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(Long id, TaskType taskType, String name, Status status, String description) {
        super(id, taskType, name, status, description);
    }

//    @Override
//    public String toString() {
//        return super.toString();
//    }
}