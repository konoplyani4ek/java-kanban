package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Epic;
import javakanban.entity.Subtask;
import javakanban.exception.ManagerSaveException;
import javakanban.exception.NotFoundException;
import javakanban.manager.task.TaskManager;
import server.HttpTaskServer;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;
    private String requestBody;

    public SubtaskHandler(TaskManager taskManager) {
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
            case GET_SUBTASKS:
                List<Subtask> subtasks = taskManager.getAllSubtasks();
                if (subtasks.isEmpty()) {
                    sendIfEmptyList(exchange);
                    return;
                }
                sendText(exchange, gson.toJson(subtasks), 200);
                break;

            case GET_SUBTASK_BY_ID:
                try {
                    int idForGet = extractIdFromPath(path);
                    Subtask subtask = taskManager.getSubtaskById(idForGet);
                    if (subtask == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    sendText(exchange, gson.toJson(subtask), 200);
                } catch (NotFoundException e) {
                    sendText(exchange, e.getMessage(), 404);
                } catch (IllegalArgumentException e) {
                    sendText(exchange, "Ошибка: неверный путь или идентификатор", 400);
                }
                break;

            case CREATE_OR_UPDATE_SUBTASK:
                createOrUpdateSubtask(exchange);
                break;

            case DELETE_SUBTASK:
                try {
                    int idForDelete = extractIdFromPath(path);
                    if (taskManager.getSubtaskById(idForDelete) == null) {
                        sendNotFound(exchange);
                        return;
                    }
                    taskManager.deleteSubtaskById(idForDelete);
                    sendText(exchange, "Подзадача с Id: " + idForDelete + " успешно удалена", 204);

                } catch (NotFoundException e) {
                    sendText(exchange, e.getMessage(), 404);
                }
            default:
                new HttpTaskServer.UnknownPathHandler().handle(exchange);
        }
    }

    private void createOrUpdateSubtask(HttpExchange exchange) throws IOException {
        try {
            Subtask incoming = gson.fromJson(requestBody, Subtask.class);

            if (incoming == null) {
                sendText(exchange, "Ошибка: тело запроса пустое или некорректное", 400);
                return;
            }

            Long id = incoming.getId();
            Long epicId = incoming.getEpicId();

            // --- Проверяем корректность Id эпика ---
            Epic parentEpic = taskManager.getEpicById(epicId);
            if (parentEpic == null) {
                sendText(exchange, "Ошибка: указан несуществующий Id эпика", 400);
                return;
            }

            // --- Создание новой подзадачи ---
            if (id == null || id == 0 || id == -1) {
                Subtask newSubtask = new Subtask(
                        incoming.getId(),
                        incoming.getName(),
                        incoming.getStatus(),
                        incoming.getDescription(),
                        incoming.getDuration(),
                        incoming.getStartTime(),
                        incoming.getEpicId()
                );

                taskManager.putNewSubtask(newSubtask);
                sendText(exchange, "Подзадача создана с Id: " + newSubtask.getId(), 201);
                return;
            }

            // --- Обновление подзадачи ---
            Subtask existing = taskManager.getSubtaskById(id);
            if (existing == null) {
                sendNotFound(exchange);
                return;
            }

            // Создаём immutable-объект Subtask
            Subtask updated = new Subtask(
                    incoming.getId(),
                    incoming.getName(),
                    incoming.getStatus(),
                    incoming.getDescription(),
                    incoming.getDuration(),
                    incoming.getStartTime(),
                    incoming.getEpicId()
            );

            taskManager.updateSubtask(updated);
            sendText(exchange, "Подзадача с Id: " + id + " успешно обновлена", 200);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);
        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные подзадачи", 400);
        }
    }

    private int extractIdFromPath(String path) {
        String[] pathParts = path.split("/");

        if (pathParts.length >= 3 && "subtasks".equals(pathParts[1])) {
            return Integer.parseInt(pathParts[2]);
        }

        throw new IllegalArgumentException("Неверный путь, идентификатор не найден");
    }
}

