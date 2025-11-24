package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Epic;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;
import server.HttpTaskServer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private String requestBody;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.getGson();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        //
        if (method.equals("POST") || method.equals("PUT") || method.equals("PATCH")) {
            requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        } else {
            requestBody = "";
        }

        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        try {
            switch (endpoint) {

                case GET_EPICS: {
                    List<Epic> epics = taskManager.getAllEpics();
                    if (epics.isEmpty()) {
                        sendEmpty(exchange, 200);
                        return;
                    }
                    sendText(exchange, gson.toJson(epics), 200);
                    return;
                }

                case GET_EPIC_BY_ID:
                    try {
                        int idForGet = extractIdFromPath(path);
                        Epic epic = taskManager.getEpicById(idForGet);
                        if (epic == null) {
                            sendNotFound(exchange);
                            return;
                        }
                        sendText(exchange, gson.toJson(epic), 200);

                    } catch (NotFoundException e) {
                        sendText(exchange, e.getMessage(), 404);
                    } catch (IllegalArgumentException e) {
                        sendText(exchange, "Ошибка: неверный путь или идентификатор", 400);
                    }
                    break;

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

                case CREATE_OR_UPDATE_EPIC:
                    createOrUpdateEpic(exchange);
                    return;

                case DELETE_EPIC: {
                    try {
                        int idForDelete = extractIdFromPath(path);
                        if (taskManager.getEpicById(idForDelete) == null) {
                            sendNotFound(exchange);
                            return;
                        }

                        taskManager.deleteEpicById(idForDelete);
                        sendText(exchange, "Эпик с Id: " + idForDelete + " успешно удален", 204);

                    } catch (NotFoundException e) {
                        sendText(exchange, e.getMessage(), 404);

                    }
                }

                default:
                    new HttpTaskServer.UnknownPathHandler().handle(exchange);
            }

        } catch (IllegalArgumentException e) {
            sendText(exchange, "Ошибка: неверный путь или идентификатор", 400);
        } catch (Exception e) {
            sendText(exchange, "Ошибка сервера: " + e.getMessage(), 500);

        }
    }

    private void createOrUpdateEpic(HttpExchange exchange) throws IOException {
        try {
            Epic incoming = gson.fromJson(requestBody, Epic.class);

            if (incoming == null) {
                sendText(exchange, "Ошибка: тело запроса пустое или некорректное", 400);
                return;
            }

            Long id = incoming.getId();

            if (id == null || id <= 0) {
                Epic newEpic = new Epic(incoming.getName(), incoming.getDescription());
                taskManager.putNewEpic(newEpic);
                sendText(exchange, "Эпик создан с Id: " + newEpic.getId(), 201);
                return;
            }

            Epic existing = taskManager.getEpicById(id);

            if (existing == null) {
                sendEmpty(exchange, 404);
                return;
            }

            Epic updated = new Epic(
                    existing.getId(),
                    incoming.getName(),
                    existing.getStatus(),
                    incoming.getDescription(),
                    existing.getDuration(),
                    existing.getStartTime(),
                    existing.getEndTime()
            );

            taskManager.updateEpic(updated);
            sendText(exchange, "Эпик с Id: " + id + " успешно обновлён", 200);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные", 400);
        }
    }

    private int extractIdFromPath(String path) {
        String[] pathParts = path.split("/");
        if (pathParts.length >= 3 && "epics".equals(pathParts[1])) {
            return Integer.parseInt(pathParts[2]);
        }
        throw new IllegalArgumentException("Неверный путь, идентификатор не найден");
    }

    private void sendEmpty(HttpExchange exchange, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.getResponseBody().close();
    }


}
