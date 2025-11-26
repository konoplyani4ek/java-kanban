package serverTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javakanban.entity.Task;
import javakanban.manager.task.InMemoryTaskManager;
import javakanban.manager.task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapter.Adapters;


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

public class PrioritizedHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new Adapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new Adapters.DurationAdapter())
            .create();
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        client = HttpClient.newHttpClient();
        taskServer.start();
    }

    @AfterEach
    public void tearDown() {
        taskServer.stop();
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Задача 1", "Описание 1");
        Task task2 = new Task("Задача 2", "Описание 2");

        task1.setStartTime(LocalDateTime.now().plusMinutes(10));
        task2.setStartTime(LocalDateTime.now().plusMinutes(20));
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);


        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized"))
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе приоритезированных задач.");

        List<Task> prioritizedTasks = gson.fromJson(response.body(), List.class);
        assertNotNull(prioritizedTasks, "Список приоритезированных задач не должен быть пустым.");
        assertEquals(2, prioritizedTasks.size(), "Количество приоритезированных задач должно быть равно 2.");
    }

    @Test // не работает
    public void testGetEmptyPrioritizedTasks() throws IOException, InterruptedException {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/prioritized")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, response.statusCode(), "Неверный код ответа при запросе пустого списка " +
                "приоритезированных задач.");
    }
}
