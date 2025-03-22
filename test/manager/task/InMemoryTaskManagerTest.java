package manager.task;

import javaKanban.entity.*;
import javaKanban.manager.task.InMemoryTaskManager;
import javaKanban.manager.task.TaskManager;
import org.junit.jupiter.api.*;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    Task buildTask() {
        return new Task("task", "description");
    }

    Epic buildEpic() {
        return new Epic("epic", "description");
    }

    Subtask buildSubtask(Epic epic) {
        return new Subtask("subtask", "description", epic.getId());
    }

    @Test
    void putNewTask_taskHashMapNotEmpty() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        assertFalse(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void putNewEpic_epicHashMapNotEmpty() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        assertFalse(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void putNewSubtask_subtaskHashMapNotEmpty() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtask(epic));
        assertFalse(taskManager.getAllSubtasks().isEmpty());
    }

    @Test
    void clearAllTasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        taskManager.clearAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void clearAllEpics() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        Epic epic2 = taskManager.putNewEpic(buildEpic());
        taskManager.clearAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void clearAllSubtasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtask(epic));
        taskManager.clearAllEpics();
        taskManager.clearAllSubtasks();
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void getById_returnTask() { // не понял комментарий про проверку айди таска
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.putNewTask(buildTask());
        assertEquals(1, task1.getId());
    }

    @Test
    void getById_returnEpic() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        assertEquals(1, epic.getId());
    }

    @Test
    void getById_returnSubtask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        assertEquals(2, subtask1.getId());
    }

    @Test
    void updateTask_changeNameValue() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.putNewTask(buildTask());
        Task updateTask1 = new Task("НоваяЗадача1", "Описание задачи1",
                task1.getId(), Status.NEW);
        taskManager.updateTask(updateTask1);
        assertEquals("НоваяЗадача1", taskManager.getTaskById(1).getName());
    }

    @Test
    void updateTask_changeStatus() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.putNewTask(buildTask());
        assertEquals(Status.NEW, task1.getStatus());
        Task updateTask1 = new Task("НоваяЗадача1", "Описание задачи1",
                task1.getId(), Status.IN_PROGRESS);
        taskManager.updateTask(updateTask1);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(1).getStatus());
    }


    @Test
    void updateSubtask_changeEpicStatusFromNewToInProgress_anySubtaskStatusInProgress() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        taskManager.updateSubtask(new Subtask("подзадача 2 обновлена", "Тема подзадачи 2 обновлена", subtask1.getId(), Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(2).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void updateSubtask_changeEpicStatusFromNewToDone_allSubtasksStatusDone() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtask(epic));
        taskManager.updateSubtask(new Subtask("subtask1", "subtask1disc", subtask1.getId(), Status.DONE, epic.getId()));
        taskManager.updateSubtask(new Subtask("subtask2", "subtask2disc", subtask2.getId(), Status.DONE, epic.getId()));
        assertEquals(Status.DONE, taskManager.getSubtaskById(2).getStatus());
        assertEquals(Status.DONE, taskManager.getSubtaskById(3).getStatus());
        assertEquals(Status.DONE, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void equals_returnTrue_sameValuesTasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        assertNotEquals(task1, task2);
        ArrayList<Task> tasks = taskManager.getAllTasks();
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
    }

    @Test
    void getHistory_returnHistory_withTasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        assertEquals(task1, taskManager.getHistory().get(0));
        assertEquals(task2, taskManager.getHistory().get(1));

    }

    @Test
    void getHistory_returnHistory_withEpics() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = taskManager.putNewEpic(buildEpic());
        Epic epic2 = taskManager.putNewEpic(buildEpic());
        taskManager.getEpicById(1);
        taskManager.getEpicById(2);
        assertEquals(epic1, taskManager.getHistory().get(0));
        assertEquals(epic2, taskManager.getHistory().get(1));
    }

    @Test
    void getHistory_returnHistory_withSubtasks() {
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic1 = taskManager.putNewEpic(buildEpic());
        Epic epic2 = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic1));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtask(epic2));
        taskManager.getSubtaskById(3);
        taskManager.getSubtaskById(4);
        assertEquals(subtask1, taskManager.getHistory().get(0));
        assertEquals(subtask2, taskManager.getHistory().get(1));
    }

    @Test
    void getHistory_max10Tasks_whenAdd15() {
        TaskManager taskManager = new InMemoryTaskManager();
        for (int i = 0; i <= 15; i++) {
            taskManager.putNewTask(buildTask());
            taskManager.getEpicById(i + 1);
        }
        assertEquals(10, taskManager.getHistory().size());
    }

    @Test
    void putNewSubtask_notAddSubtask_withIdOfEpic() { // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
        TaskManager taskManager = new InMemoryTaskManager();
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask = taskManager.putNewSubtask(buildSubtask(epic));
        assertNotEquals(epic.getId(), subtask.getId());
    }
}
