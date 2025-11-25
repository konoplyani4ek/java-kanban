package server.handler;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Epic;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.util.Objects;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    private String requestBody;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if (method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) {
            requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            requestBody = "";
        }

        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        try {
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

    private void createOrUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic incoming = gson.fromJson(this.requestBody, Epic.class);

            if (incoming == null) {
                throw new NotFoundException("Тело запроса пустое или некорректное");
            }

            //  Создание
            if (incoming.getId() == null || incoming.getId() <= 0) {
                Epic newEpic = taskManager.putNewEpic(incoming);
                sendText(exchange, "Эпик создан с Id: " + newEpic.getId(), 201);
                return;
            }

            // Обновление
            Epic updated = taskManager.updateEpic(incoming);
            sendText(exchange, "Эпик с Id: " + updated.getId() + " успешно обновлён", 200);

        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);

        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные", 400);
        }
    }

    private void sendEmpty(HttpExchange exchange, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().close();
    }

    private void handleGet(HttpExchange exchange, Endpoint endpoint, String path) throws IOException {
        switch (endpoint) {

            case GET_EPICS: {
                sendText(exchange, gson.toJson(taskManager.getAllEpics()), 200);
                return;
            }

            case GET_EPIC_BY_ID: {
                int idForGet = extractIdFromPath(path);
                Epic epic = taskManager.getEpicById(idForGet);

                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }

                sendText(exchange, gson.toJson(epic), 200);
                return;
            }

            case GET_EPIC_SUBTASKS: {
                int id = extractIdFromPath(path);
                Epic epic = taskManager.getEpicById(id);

                if (epic == null) {
                    sendEmpty(exchange, 404);
                    return;
                }

                sendText(exchange, gson.toJson(taskManager.getSubtasksByEpic(epic)), 200);
                return;
            }

            default:
                new BaseHttpHandler.UnknownPathHandler().handle(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, Endpoint endpoint) throws IOException {
        if (Objects.requireNonNull(endpoint) == Endpoint.CREATE_OR_UPDATE_EPIC) {
            createOrUpdateEpic(exchange);
        } else {
            new UnknownPathHandler().handle(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Endpoint endpoint, String path) throws IOException {
        switch (endpoint) {

            case DELETE_EPIC: {
                int idForDelete = extractIdFromPath(path);

                if (taskManager.getEpicById(idForDelete) == null) {
                    sendNotFound(exchange);
                    return;
                }

                taskManager.deleteEpicById(idForDelete);
                sendText(exchange, "Эпик с Id: " + idForDelete + " успешно удален", 204);
                return;
            }

            default:
                new BaseHttpHandler.UnknownPathHandler().handle(exchange);
        }
    }
}
