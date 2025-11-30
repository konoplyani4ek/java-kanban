package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Task;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {

                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange, path);
                case "DELETE" -> handleDelete(exchange, path);

                default -> sendNotFound(exchange);
            }

        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);

        } catch (IllegalArgumentException e) {
            sendText(exchange, "Ошибка: неверный путь или идентификатор", 400);

        } catch (Exception e) {
            sendText(exchange, "Ошибка сервера: " + e.getMessage(), 500);
        }
    }

    private void handleGet(HttpExchange exchange, String path) throws IOException {

        if (path.equals("/tasks")) {
            sendText(exchange, gson.toJson(taskManager.getAllTasks()), 200);
            return;
        }

        if (path.matches("/tasks/\\d+")) {
            int id = extractIdFromPath(path);
            Task task = taskManager.getTaskById(id);

            if (task == null) {
                sendNotFound(exchange);
                return;
            }

            sendText(exchange, gson.toJson(task), 200);
            return;
        }
        sendNotFound(exchange);
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        if ("/tasks".equals(path)) {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            createOrUpdateTask(exchange, body);
            return;
        }
        sendNotFound(exchange);
    }


    private void createOrUpdateTask(HttpExchange exchange, String body) throws IOException {
        try {
            Task incoming = gson.fromJson(body, Task.class);

            if (incoming == null) {
                sendText(exchange, "Ошибка: тело запроса пустое или некорректное", 400);
                return;
            }

            Long id = incoming.getId();

            // === Создание ===
            if (id == null || id <= 0) {
                Task newTask = taskManager.putNewTask(incoming);
                sendText(exchange, "Задача создана с Id: " + newTask.getId(), 201);
                return;
            }

            // === Обновление ===
            Task updated = taskManager.updateTask(incoming);
            sendText(exchange, "Задача с Id: " + updated.getId() + " успешно обновлена", 200);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);

        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);

        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные задачи", 400);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {

        if (!path.matches("/tasks/\\d+")) {
            sendNotFound(exchange);
            return;
        }
        int id = extractIdFromPath(path);
        if (taskManager.getTaskById(id) == null) {
            sendNotFound(exchange);
            return;
        }
        taskManager.deleteTaskById(id);
        sendText(exchange, "Задача с Id: " + id + " успешно удалена", 204);
    }
}
