package server.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Task;
import javakanban.manager.task.TaskManager;
import server.adapter.Adapters;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (!method.equals("GET")) {
            sendNotFound(exchange);
            return;
        }

        if (path.equals("/prioritized")) {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

            if (prioritizedTasks.isEmpty()) {
                // Пустой список → 204 No Content без тела
                exchange.sendResponseHeaders(204, -1);
            } else {
                // Есть задачи → 200 OK + JSON
                sendText(exchange, gson.toJson(prioritizedTasks), 200);
            }
            return;
        }

        sendNotFound(exchange);
    }
}
