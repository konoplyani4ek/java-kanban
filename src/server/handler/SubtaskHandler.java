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

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private String requestBody;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // тело запроса только для POST/PUT/PATCH
        requestBody = switch (method) {
            case "POST", "PUT", "PATCH" -> new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            default -> "";
        };

        Endpoint endpoint = Endpoint.endpointFromMethodAndPath(method, path);

        try {
            switch (method) {
                case "GET" -> handleGet(exchange, endpoint, path);
                case "POST" -> handlePost(exchange, endpoint);
                case "DELETE" -> handleDelete(exchange, endpoint, path);
                default -> new BaseHttpHandler.UnknownPathHandler().handle(exchange);
            }

        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);

        } catch (IllegalArgumentException e) {
            sendText(exchange, "Ошибка: неверный путь или идентификатор", 400);

        } catch (Exception e) {
            sendText(exchange, "Ошибка сервера: " + e.getMessage(), 500);
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

            // === Проверяем корректность Id эпика ===
            Epic parentEpic = taskManager.getEpicById(epicId);
            if (parentEpic == null) {
                sendText(exchange, "Ошибка: указан несуществующий Id эпика", 400);
                return;
            }

            // === Создание новой подзадачи ===
            if (id == null || id <= 0) {
                Subtask newSubtask = taskManager.putNewSubtask(incoming);
                sendText(exchange, "Подзадача создана с Id: " + newSubtask.getId(), 201);
                return;
            }

            // === Обновление подзадачи ===
            Subtask updated = taskManager.updateSubtask(incoming);
            sendText(exchange, "Подзадача с Id: " + updated.getId() + " успешно обновлена", 200);

        } catch (NotFoundException e) {
            sendText(exchange, e.getMessage(), 404);

        } catch (ManagerSaveException e) {
            sendHasInteractions(exchange);

        } catch (Exception e) {
            sendText(exchange, "Ошибка: некорректный JSON или данные подзадачи", 400);
        }
    }

    private void handleGet(HttpExchange exchange, Endpoint endpoint, String path) throws IOException {
        switch (endpoint) {

            case GET_SUBTASKS -> {
                sendText(exchange, gson.toJson(taskManager.getAllSubtasks()), 200);
            }

            case GET_SUBTASK_BY_ID -> {
                int id = extractIdFromPath(path);
                Subtask subtask = taskManager.getSubtaskById(id);

                if (subtask == null) {
                    sendNotFound(exchange);
                    return;
                }

                sendText(exchange, gson.toJson(subtask), 200);
            }

            default -> new BaseHttpHandler.UnknownPathHandler().handle(exchange);
        }
    }

    private void handlePost(HttpExchange exchange, Endpoint endpoint) throws IOException {
        switch (endpoint) {

            case CREATE_OR_UPDATE_SUBTASK -> {
                createOrUpdateSubtask(exchange);
            }

            default -> new BaseHttpHandler.UnknownPathHandler().handle(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Endpoint endpoint, String path) throws IOException {
        switch (endpoint) {

            case DELETE_SUBTASK -> {
                int id = extractIdFromPath(path);
                if (taskManager.getSubtaskById(id) == null) {
                    sendNotFound(exchange);
                    return;
                }

                taskManager.deleteSubtaskById(id);
                sendText(exchange, "Подзадача с Id: " + id + " успешно удалена", 204);
            }

            default -> new BaseHttpHandler.UnknownPathHandler().handle(exchange);
        }
    }
}

