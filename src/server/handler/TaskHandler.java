package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Task;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;
import server.HttpTaskServer;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private String requestBody;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        switch (endpoint) {
            case GET_TASKS:
                List<Task> tasks = taskManager.getAllTasks();
                if (tasks.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }
                sendText(exchange, gson.toJson(tasks), 200);
                break;

            case GET_TASK_BY_ID:
                try {
                    int idForGet = extractIdFromPath(path);
                    Task task = taskManager.getTaskById(idForGet);
                    if (task == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(task), 200);
                } catch (NotFoundException e) {
                    sendText(exchange, e.getMessage(), 404);

                } catch (IllegalArgumentException e) {
                    sendText(exchange, "Ошибка: неверный путь или идентификатор", 400);
                }
                break;

            case CREATE_OR_UPDATE_TASK:
                createOrUpdateTask(exchange);
                break;

            case DELETE_TASK:
                try {
                    int idForDelete = extractIdFromPath(path);
                    if (taskManager.getTaskById(idForDelete) == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    taskManager.deleteTaskById(idForDelete);
                    sendText(exchange, "Задача с Id: " + idForDelete + " успешно удалена", 204);

                } catch (NotFoundException e) {
                    sendText(exchange, e.getMessage(), 404);
                }

            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }

    private void createOrUpdateTask(HttpExchange exchange) throws IOException {
        try {
            Task incoming = gson.fromJson(requestBody, Task.class);

            if (incoming == null) {
                sendText(exchange, "Ошибка: тело запроса пустое или некорректное", 400);
                return;
            }

            Long id = incoming.getId();

            // --- Создание новой задачи ---
            if (id == null || id == 0 || id == -1) {
                Task newTask = new Task(
                        incoming.getId(),
                        incoming.getName(),
                        incoming.getStatus(),
                        incoming.getDescription(),
                        incoming.getDuration(),
                        incoming.getStartTime()
                );

                taskManager.putNewTask(newTask);
                sendText(exchange, "Задача создана с Id: " + newTask.getId(), 201);
                return;
            }

            // --- Обновление существующей задачи ---
            Task existing = taskManager.getTaskById(id);
            if (existing == null) {
                sendNotFound(exchange);
                return;
            }

            // immutable-обновление
            Task updated = new Task(
                    incoming.getId(),
                    incoming.getName(),
                    incoming.getStatus(),
                    incoming.getDescription(),
                    incoming.getDuration(),
                    incoming.getStartTime()
            );

            taskManager.updateTask(updated);
            sendText(exchange, "Задача с Id: " + id + " успешно обновлена", 200);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);

        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные задачи", 400);
        }
    }


    private int extractIdFromPath(String path) {
        String[] pathParts = path.split("/");

        if (pathParts.length >= 3 && "tasks".equals(pathParts[1])) {
            return Integer.parseInt(pathParts[2]);
        }

        throw new IllegalArgumentException("Неверный путь, идентификатор не найден");
    }
}