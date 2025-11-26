package serverTest;

import com.google.gson.Gson;

import javakanban.entity.Status;
import javakanban.entity.Task;
import javakanban.exception.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;

public class TaskHandlerTest {

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
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Тестовая задача", "Описание задачи");
        String taskJson = gson.toJson(task);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/tasks", taskJson);
        assertEquals(201, response.statusCode(), "Неверный код ответа при добавлении задачи.");

        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks, "Список задач не должен быть пуст.");
        assertEquals(1, tasks.size(), "Количество задач должно быть равно 1.");
        assertEquals("Тестовая задача", tasks.get(0).getName(), "Имя задачи не совпадает.");
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {
        taskManager.putNewTask(new Task("Задача 1", "Описание 1"));
        taskManager.putNewTask(new Task("Задача 2", "Описание 2"));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks");
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе всех задач.");

        List<Task> tasks = gson.fromJson(response.body(), List.class);
        assertNotNull(tasks, "Список задач не должен быть пуст.");
        assertEquals(2, tasks.size(), "Количество задач должно быть равно 2.");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task = taskManager.putNewTask(new Task("Задача 1", "Описание 1"));

        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks/" + task.getId());
        assertEquals(200, response.statusCode(), "Неверный код ответа при запросе задачи по ID.");

        Task task2 = gson.fromJson(response.body(), Task.class);
        assertNotNull(task, "Задача не должна быть null.");
        assertEquals("Задача 1", task2.getName(), "Имя задачи не совпадает.");
    }

    @Test // не работает
    public void testGetTaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendGetRequest("http://localhost:8080/tasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для несуществующей задачи.");
    }

    @Test //expected 201 act 200
    public void testUpdateTask() throws IOException, InterruptedException {
        Task task = taskManager.putNewTask(new Task("Задача 1", "Описание 1"));
        Task updatedTask = new Task(task.getId(), "Обновленная задача", Status.IN_PROGRESS, "Обновленное описание",
                Duration.ofMinutes(45), LocalDateTime.now());
        String taskJson = gson.toJson(updatedTask);

        HttpResponse<String> response = sendPostRequest("http://localhost:8080/tasks", taskJson);
        assertEquals(200, response.statusCode(), "Неверный код ответа при обновлении задачи.");

        Task task2 = taskManager.getTaskById(task.getId());
        assertNotNull(task, "Задача не должна быть null.");
        assertEquals("Обновленная задача", task2.getName(), "Имя задачи не совпадает после обновления.");
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        Task task = taskManager.putNewTask(new Task("Задача 1", "Описание 1"));
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks/" + task.getId());
        assertEquals(204, response.statusCode(), "Неверный код ответа при удалении задачи.");
        assertThrows(NotFoundException.class, () -> taskManager.getTaskById(task.getId()),
                "Ожидается NotFoundException после удаления задачи");
    }


    @Test // не работает
    public void testDeleteTaskByIdNotFound() throws IOException, InterruptedException {
        HttpResponse<String> response = sendDeleteRequest("http://localhost:8080/tasks/999");
        assertEquals(404, response.statusCode(), "Ожидается код 404 для удаления несуществующей задачи.");
    }
}