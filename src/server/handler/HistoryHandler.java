package server.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import javakanban.entity.Task;
import javakanban.manager.task.TaskManager;
import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("GET".equals(method)) {
            if ("/history".equals(path)) {
                List<Task> history = taskManager.getHistory();
                if (history.isEmpty()) {
                    exchange.sendResponseHeaders(204, -1);
                } else {
                    sendText(exchange, gson.toJson(history), 200);
                }
                return;
            }
        }
        sendNotFound(exchange);
    }
}

