package mainPackage.entity;

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
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }


    @Override
    public String toString() {
        return super.toString() +
                " subtasksId=" + subtasksId +
                '}';
    }
}