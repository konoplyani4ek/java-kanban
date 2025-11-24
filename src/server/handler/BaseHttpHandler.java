package server.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler {

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (var os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendIfEmptyList(HttpExchange exchange) throws IOException {
        String response = "[]";
        sendText(exchange, response, 200);
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Ошибка: путь не найден";
        sendText(exchange, response, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Ошибка: менеджер не может сохранить данные";
        sendText(exchange, response, 500);
    }
}
