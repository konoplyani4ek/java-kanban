package entity;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Long> subtasksId;

    public ArrayList<Long> getSubtasksId() {
        return subtasksId;
    }



    public Epic(String name, String description){
        super(name, description);
        this.subtasksId = new ArrayList<>();
    }

    public Epic(String name, String description, long id, Status status, ArrayList<Long> subtasksId) {
        super(name, description, id, status);
        this.subtasksId = subtasksId;
    }

    public Epic(String name, String description, ArrayList<Long> subtasksId) {
        super(name, description);
        this.subtasksId = subtasksId;
    }

    @Override
    public String toString() {
        return super.toString() +
                " subtasksId=" + subtasksId +
                '}';
    }
}