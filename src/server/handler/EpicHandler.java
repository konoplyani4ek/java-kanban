package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Epic;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
            if (path.equals("/epics")) {
                sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                return;
            }

            if (path.matches("/epics/\\d+")) {
                int id = extractIdFromPath(path);
                Epic epic = taskManager.getEpicById(id); // NotFoundException выбросит 404
                sendText(exchange, gson.toJson(epic), 200);
                return;
            }

            if (path.matches("/epics/\\d+/subtasks")) {
                int id = extractIdFromPath(path.split("/subtasks")[0]);
                Epic epic = taskManager.getEpicById(id); // NotFoundException выбросит 404
                sendText(exchange, gson.toJson(taskManager.getSubtasksByEpicId(epic.getId())), 200);
                return;
            }
            sendNotFound(exchange);
    }

    private void handlePost(HttpExchange exchange, String path) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

        if (path.equals("/epics")) {
            createOrUpdateEpic(exchange, body);
            return;
        }
        sendNotFound(exchange);
    }

    private void createOrUpdateEpic(HttpExchange exchange, String body) throws IOException {
        try {
            Epic incoming = gson.fromJson(body, Epic.class);

            if (incoming == null) {
                throw new NotFoundException("Тело запроса пустое или некорректное");
            }
            // Создание
            if (incoming.getId() == null || incoming.getId() <= 0) {
                Epic newEpic = taskManager.putNewEpic(incoming);
                sendText(exchange, "Эпик создан с Id: " + newEpic.getId(), 201);
                return;
            }

            // Обновление
            Epic updated = taskManager.updateEpic(incoming);
            sendText(exchange, "Эпик с Id: " + updated.getId() + " успешно обновлён", 200);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);

        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные", 400);
        }
    }

    private void handleDelete(HttpExchange exchange, String path) throws IOException {
        if (path.matches("/epics/\\d+")) {
            int id = extractIdFromPath(path);
            taskManager.deleteEpicById(id);
            sendText(exchange, "Эпик с Id: " + id + " успешно удален", 204);
            return;
        }
        sendNotFound(exchange);
    }

}
