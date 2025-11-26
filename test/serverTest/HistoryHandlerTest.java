package serverTest;

import com.google.gson.Gson;

import javakanban.entity.Status;
import javakanban.entity.Task;
import javakanban.manager.task.InMemoryTaskManager;
import javakanban.manager.task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.handler.BaseHttpHandler;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HistoryHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = BaseHttpHandler.getGson();
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);

        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/history")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе истории задач.");

        List<Task> history = gson.fromJson(response.body(), List.class);
        assertNotNull(history, "История задач не должна быть пустой.");
        assertEquals(2, history.size(), "История должна содержать 2 задачи.");
    }

    @Test // exp 204 acutal 200
    public void testGetEmptyHistory() throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/history")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, response.statusCode(), "Неверный код ответа при запросе пустой истории.");
    }
}