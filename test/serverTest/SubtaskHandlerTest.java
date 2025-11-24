package serverTest;

import com.google.gson.Gson;

import javakanban.entity.Epic;
import javakanban.entity.Status;
import javakanban.entity.Subtask;
import javakanban.manager.task.InMemoryTaskManager;
import javakanban.manager.task.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskHandlerTest {

    private TaskManager taskManager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
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
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private HttpResponse<String> sendDeleteRequest(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = taskManager.putNewEpic(new Epic("Тестовый эпик", "Описание эпика"));
        Subtask subtask = new Subtask("Тестовая подзадача", "Описание подзадачи", epic.getId());
        String subtaskJson = gson.toJson(subtask);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/subtasks", subtaskJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении подзадачи.");

        List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertNotNull(subtasks, "Список подзадач не должен быть пуст.");
        assertEquals(1, subtasks.size(), "Количество подзадач должно быть равно 1.");
        assertEquals("Тестовая подзадача", subtasks.get(0).getName(), "Имя подзадачи не совпадает.");
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {
        Epic epic = taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));
        taskManager.putNewSubtask(new Subtask("Подзадача 1", "Описание 1", epic.getId()));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks");
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе всех подзадач.");

        List<Subtask> subtasks = gson.fromJson(response.body(), List.class);
        assertNotNull(subtasks, "Список подзадач не должен быть пуст.");
        assertEquals(1, subtasks.size(), "Количество подзадач должно быть равно 1.");
    }

    @Test // не проходит
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epic = taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));
        Subtask subtask = taskManager.putNewSubtask(new Subtask("Подзадача 1", "Описание 1", epic.getId()));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks/" + subtask.getId());
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе подзадачи по ID.");

        Subtask subtask2 = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(subtask, "Подзадача не должна быть null.");
        assertEquals("Подзадача 1", subtask2.getName(), "Имя подзадачи не совпадает.");
    }

    @Test // не проходит
    public void testGetSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/subtasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для несуществующей подзадачи.");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));
        Subtask subtask = taskManager.putNewSubtask(new Subtask("Подзадача 1", "Описание 1", epic.getId()));
        Subtask updatedSubtask = new Subtask(subtask.getId(), "Обновленная подзадача", Status.DONE,
                "Обновленное описание", Duration.ofMinutes(45), LocalDateTime.now(), epic.getId());
        String subtaskJson = gson.toJson(updatedSubtask);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/subtasks", subtaskJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при обновлении подзадачи.");

        Subtask subtask2 = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(subtask, "Подзадача не должна быть null.");
        assertEquals("Обновленная подзадача", subtask.getName(), "Имя подзадачи не совпадает после обновления.");
    }

    @Test // exp 201, actual 200
    public void testDeleteSubtaskById() throws IOException, InterruptedException {
        Epic epic = taskManager.putNewEpic(new Epic("Эпик 1", "Описание 1"));
        Subtask subtask = taskManager.putNewSubtask(new Subtask("Подзадача 1", "Описание 1", epic.getId()));

        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/subtasks/" + subtask.getId());
        assertEquals(204, response.statusCode(), "Неверный код ответа при удалении подзадачи.");

        Subtask subtask2 = taskManager.getSubtaskById(subtask.getId());
        assertNull(subtask, "Подзадача должна быть null после удаления.");
    }

    @Test // не проходит
    public void testDeleteSubtaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/subtasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для удаления несуществующей подзадачи.");
    }
}