package server.handler;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Task;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private String requestBody;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

            switch (method) {

                case "GET":
                    handleGet(exchange, endpoint, path);
                    return;

                case "POST":
                    handlePost(exchange, endpoint);
                    return;

                case "DELETE":
                    handleDelete(exchange, endpoint, path);
                    return;

                default:
                    new BaseHttpHandler.UnknownPathHandler().handle(exchange);
            }

        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);

        } catch (IllegalArgumentException e) {
            sendText(exchange, "Ошибка: неверный путь или идентификатор", 400);

        } catch (Exception e) {
            sendText(exchange, "Ошибка сервера: " + e.getMessage(), 500);
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

            // === Создание новой задачи ===
            if (id == null || id <= 0) {
                Task newTask = taskManager.putNewTask(incoming);
                sendText(exchange, "Задача создана с Id: " + newTask.getId(), 201);
                return;
            }

            // === Обновление задачи ===
            Task updated = taskManager.updateTask(incoming);
            sendText(exchange, "Задача с Id: " + updated.getId() + " успешно обновлена", 200);

        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);

        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные задачи", 400);
        }
    }

    private void handleGet(HttpExchange exchange, Endpoint endpoint, String path) throws IOException {
        switch (endpoint) {

            case GET_TASKS: {
                sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
                return;
            }

            case GET_TASK_BY_ID: {
                int id = extractIdFromPath(path);
                Task task = taskManager.getTaskById(id);

                if (task == null) {
                    sendNotFound(exchange);
                    return;
                }

                sendText(exchange, gson.toJson(task), 200);
                return;
            }

            default:
                new BaseHttpHandler.UnknownPathHandler().handle(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, Endpoint endpoint) throws IOException {
        if (Objects.requireNonNull(endpoint) == Endpoint.CREATE_OR_UPDATE_TASK) {
            createOrUpdateTask(exchange);
            return;
        } else {
            new UnknownPathHandler().handle(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Endpoint endpoint, String path) throws IOException {
        if (Objects.requireNonNull(endpoint) == Endpoint.DELETE_TASK) {
            int id = extractIdFromPath(path);

            if (taskManager.getTaskById(id) == null) {
                sendNotFound(exchange);
                return;
            }
            taskManager.deleteTaskById(id);
            sendText(exchange, "Задача с Id: " + id + " успешно удалена", 204);

        } else {
            new UnknownPathHandler().handle(exchange);
        }
    }



}