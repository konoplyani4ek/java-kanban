package javakanban.manager;

import javakanban.entity.*;

public class CsvConverter {

    public static Task fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Пустая или null строка не может быть распарсена");
        }
        String[] elements = value.split(",");
        Long id = Long.parseLong(elements[0]);
        TaskType taskType = TaskType.valueOf(elements[1]);
        String name = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];


        switch (taskType) {
            case TASK:
                return new Task(id, taskType, name, status, description);
            case EPIC:
                return new Epic(id, taskType, name, status, description);
            case SUBTASK:
                Long epicId = Long.parseLong(elements[5]);
                return new Subtask(id, taskType, name, status, description, epicId);
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }






}
