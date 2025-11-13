package javakanban.manager;

import javakanban.entity.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CsvConverter {

    public static Task fromString(String value) { // пришлось добавить много проверок на null чтобы не исправлять старые тесты
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Пустая или null строка не может быть распарсена");
        }

        String[] elements = value.split(",", -1);

        Long id = elements[0].isBlank() ? null : Long.parseLong(elements[0]);
        TaskType taskType = TaskType.valueOf(elements[1]);
        String name = elements[2];
        Status status = Status.valueOf(elements[3]);
        String description = elements[4];
        Duration duration = elements[5].isBlank() ? null : Duration.ofMinutes(Long.parseLong(elements[5]));
        LocalDateTime startTime = elements[6].isBlank() ? null : LocalDateTime.parse(elements[6]);

        return switch (taskType) {
            case TASK -> new Task(id, name, status, description, duration, startTime);
            case EPIC -> {
                LocalDateTime endTime = (elements.length > 7 && !elements[7].isBlank())
                        ? LocalDateTime.parse(elements[7])
                        : null;// может быть пустым
                yield new Epic(id, name, status, description, duration, startTime, endTime);
            }
            case SUBTASK -> {
                Long epicId = (elements.length > 7 && !elements[7].isBlank())
                        ? Long.parseLong(elements[7])
                        : null; //  может быть пустым
                yield new Subtask(id, name, status, description, duration, startTime, epicId);
            }
        };
    }

    public static String toStringCSV(Task task) {

        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";
        return String.format("%d,%s,%s,%s,%s,%s,%s%n",
                task.getId(),
                task.getTaskType(),
                task.getName(),
                task.getStatus(),
                task.getDescription(),
                duration,
                startTime);
    }

    public static String toStringCSV(Epic epic) {

        String duration = epic.getDuration() != null ? String.valueOf(epic.getDuration().toMinutes()) : "";
        String startTime = epic.getStartTime() != null ? epic.getStartTime().toString() : "";
        String endTime = epic.getEndTime() != null ? epic.getEndTime().toString() : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s%n",
                epic.getId(),
                epic.getTaskType(),
                epic.getName(),
                epic.getStatus(),
                epic.getDescription(),
                duration,
                startTime,
                endTime);
    }

    public static String toStringCSV(Subtask subtask) {

        String duration = subtask.getDuration() != null ? String.valueOf(subtask.getDuration().toMinutes()) : "";
        String startTime = subtask.getStartTime() != null ? subtask.getStartTime().toString() : "";
        String epicId = subtask.getEpicId() != null ? String.valueOf(subtask.getEpicId()) : "";

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                subtask.getId(),
                subtask.getTaskType(),
                subtask.getName(),
                subtask.getStatus(),
                subtask.getDescription(),
                duration,
                startTime,
                epicId);
    }
}
