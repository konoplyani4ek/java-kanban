package serverTest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javakanban.entity.Epic;
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

import static org.junit.jupiter.api.Assertions.*;

public class EpicHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
//    private Gson gson;
    private HttpClient client;
    Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new Adapters.LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new Adapters.DurationAdapter())
            .create();


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

    private HttpResponse<String> sendPostRequest(String url, String jsonBody) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendGetRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        // Создаём локальный Gson для теста

        Epic epic = new Epic("Тестовый эпик", "Описание эпика");

        // Преобразуем объект в JSON
        String epicJson = gson.toJson(epic);

        // Отправляем POST-запрос
        HttpResponse<String> response = sendPostRequest("http://localhost:8080/epics", epicJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении эпика.");

        // Проверяем состояние менеджера
        List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size());
        assertEquals("Тестовый эпик", epics.get(0).getName());
    }


    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {
        taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));
        taskManager.putNewEpic(new Epic("Эпик 2", "Описание 2"));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics");
        assertEquals(200, response.statusCode());

        Epic[] epics = gson.fromJson(response.body(), Epic[].class);
        assertEquals(2, epics.length);
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epic = taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/" + epic.getId());
        assertEquals(200, response.statusCode());

        Epic epic2 = gson.fromJson(response.body(), Epic.class);
        assertEquals("Эпик 1", epic2.getName());
    }

    @Test // expt 404 actual 500
    public void testGetEpicByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/epics/999");
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {
        Epic original = taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));

        // объект с новыми полями и тем же ID
        String json = String.format(
                "{\"id\": %d, \"name\": \"%s\", \"description\": \"%s\"}",
                original.getId(),
                "Обновленный",
                "Новое описание"
        );

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/epics", json);
        assertEquals(200, response.statusCode()); // для обновления сервер возвращает 200

        Epic updated = taskManager.getEpicById(original.getId());
        assertEquals("Обновленный", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
    }

    @Test
    public void testDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/epics/" + epic.getId());
        assertEquals(204, response.statusCode());
    }

    @Test // exp 404, actual 500
    public void testDeleteEpicByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/epics/999");
        assertEquals(404, response.statusCode());
    }
}
