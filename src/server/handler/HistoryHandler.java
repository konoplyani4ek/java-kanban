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

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;


    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(LocalDateTime.class, new Adapters.LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new Adapters.DurationAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        if (!method.equals("GET")) {
            sendNotFound(exchange);
            return;
        }
        if (path.equals("/history")) {
            List<Task> history = taskManager.getHistory();
            if (history.isEmpty()) {
                exchange.sendResponseHeaders(204, -1);
                return;
            } else {
                sendText(exchange, gson.toJson(history), 200);
                return;
            }
        }
        sendNotFound(exchange);
    }


}

