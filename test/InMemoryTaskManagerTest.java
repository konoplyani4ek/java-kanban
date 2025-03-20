import javaKanban.HistoryManager;
import javaKanban.InMemoryTaskManager;
import javaKanban.Managers;
import javaKanban.TaskManager;
import javaKanban.entity.Epic;
import javaKanban.entity.Status;
import javaKanban.entity.Subtask;
import javaKanban.entity.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private TaskManager taskManager = Managers.getDefault();
    private HistoryManager historyManager = Managers.getDefaultHistory();
    private Task task1;
    private Task task2;
    private Epic epic;

    @BeforeEach
    void createObjects() {
        task1 = new Task("Задача1", "Тема задачи 1");
        task2 = new Task("Задача2", "Тема задачи 2");
        epic = new Epic("Эпик1", "Тема эпика 1");
    }

    @Test
    void shouldCreateAndReturnObjects() {
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);
        taskManager.putNewEpic(epic);
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
        assertEquals(3, epic.getId());
    }

    @Test
    void shouldUpdateTask() {
        taskManager.putNewTask(task1);
        Task updateTask1 = new Task("НоваяЗадача1", "Описание задачи1",
                task1.getId(), Status.IN_PROGRESS);
        taskManager.updateTask(updateTask1);
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        assertEquals("НоваяЗадача1", tasks.getFirst().getName());
    }

    @Test
    void shouldRemoveAllTasks(){
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);
        taskManager.clearAllTasks();
        List<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    void shouldUpdateSubtaskAndChangeEpicStatus(){
        taskManager.putNewEpic(epic);
        Subtask subtask1 = new Subtask("подзадача1", " Тема подзадачи 1", epic.getId());
        Subtask subtask2 = new Subtask("подзадача2", " Тема подзадачи 2", epic.getId());
        assertEquals(Status.NEW, epic.getStatus());
        taskManager.putNewSubtask(subtask1);
        taskManager.putNewSubtask(subtask2);
        taskManager.updateSubtask(new Subtask("подзадача 2 обновлена", "Тема подзадачи 2 обновлена", 3, Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldKeepHistory(){
        taskManager.putNewEpic(epic);
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(epic);
        tasks.add(task1);
        tasks.add(task2);
        taskManager.getEpicById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        System.out.println(tasks);
        System.out.println(historyManager.getHistory());
        assertArrayEquals(tasks.toArray(), historyManager.getHistory().toArray());
    }
}
