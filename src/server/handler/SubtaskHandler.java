package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Epic;
import javakanban.entity.Subtask;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
        // GET /subtasks
        if (path.equals("/subtasks")) {
            List<Subtask> list = taskManager.getAllSubtasks();
            sendText(exchange, gson.toJson(list), 200);
            return;
        }

        // GET /subtasks/{id}
        if (path.startsWith("/subtasks/")) {
            int id = extractIdFromPath(path);
            Subtask subtask = taskManager.getSubtaskById(id);

            if (subtask == null) {
                sendNotFound(exchange);
                return;
            }

            sendText(exchange, gson.toJson(subtask), 200);
            return;
        }

        sendNotFound(exchange);
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        // POST /subtasks
        if (!path.equals("/subtasks")) {
            sendNotFound(exchange);
            return;
        }

        String requestBody = readBody(exchange);
        Subtask incoming = gson.fromJson(requestBody, Subtask.class);

        if (incoming == null) {
            sendText(exchange, "Ошибка: некорректное тело запроса", 400);
            return;
        }

        Long epicId = incoming.getEpicId();
        Epic epic = taskManager.getEpicById(epicId);

        if (epic == null) {
            sendText(exchange, "Ошибка: такого эпика не существует", 400);
            return;
        }

        try {
            // === Создание ===
            if (incoming.getId() == null || incoming.getId() <= 0) {
                Subtask created = taskManager.putNewSubtask(incoming);
                sendText(exchange, "Создано. Id: " + created.getId(), 201);
                return;
            }

            // === Обновление ===
            Subtask updated = taskManager.updateSubtask(incoming);
            sendText(exchange, "Обновлено. Id: " + updated.getId(), 201);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        // DELETE /subtasks/{id}
        if (path.startsWith("/subtasks/")) {
            int id = extractIdFromPath(path);

            if (taskManager.getSubtaskById(id) == null) {
                sendNotFound(exchange);
                return;
            }

            taskManager.deleteSubtaskById(id);
            sendText(exchange, "Удалено. Id: " + id, 204);
            return;
        }

        sendNotFound(exchange);
    }

    private String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Override
    protected int extractIdFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length >= 3 && "subtasks".equals(parts[1])) {
            return Integer.parseInt(parts[2]);
        }
        throw new IllegalArgumentException("Неверный путь для подзадачи, идентификатор не найден");
    }
}

