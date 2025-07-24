package manager.task;

import javakanban.entity.*;
import javakanban.manager.history.InMemoryHistoryManager;
import javakanban.manager.task.InMemoryTaskManager;
import javakanban.manager.task.TaskManager;
import org.junit.jupiter.api.*;

import java.util.ArrayList;


import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    TaskManager taskManager;

    Task buildTask() {
        return new Task("task", "description");
    }

    Epic buildEpic() {
        return new Epic("epic", "description");
    }

    Subtask buildSubtask(Epic epic) {
        return new Subtask("subtask", "description", epic.getId());
    }


    @BeforeEach
    void initTaskManager() {
        taskManager = new InMemoryTaskManager(new InMemoryHistoryManager());

    }

    @Test
    void putNewTask_taskHashMapHasElements() {
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        assertEquals(2, taskManager.getAllTasks().size());
    }

    @Test
    void putNewEpic_epicHashMapHasElement() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        assertEquals(1, taskManager.getAllEpics().size());
        assertFalse(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void putNewSubtask_subtaskHashMapHasElements() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtask(epic));
        assertEquals(2, taskManager.getAllSubtasks().size());
    }

    @Test
    void clearAllTasks_mapIsEmpty_deleteTasks() {
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        taskManager.clearAllTasks();
        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void clearAllEpics_mapIsEmpty_deleteEpics() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Epic epic2 = taskManager.putNewEpic(buildEpic());
        taskManager.clearAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void clearAllSubtasks_mapIsEmpty_deleteEpics() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtask(epic));
        taskManager.clearAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void clearAllSubtasks_mapIsEmpty_deleteSubtasks() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtask(epic));
        taskManager.clearAllSubtasks();
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void getById_returnTask() {
        Task task1 = taskManager.putNewTask(buildTask());
        assertEquals("task", task1.getName());
        assertEquals("description", task1.getDescription());
        assertEquals(1, task1.getId()); // проверка на null всегда false
    }

    @Test
    void getById_returnEpic() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        assertEquals("epic", epic.getName());
        assertEquals("description", epic.getDescription());
        assertEquals(1, epic.getId());
    }

    @Test
    void getById_returnSubtask() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        assertEquals("subtask", subtask1.getName());
        assertEquals("description", subtask1.getDescription());
        assertEquals(2, subtask1.getId());
    }

    @Test
    void updateTask_changeNameValue() {
        Task task1 = taskManager.putNewTask(buildTask());
        Task updateTask1 = new Task("НоваяЗадача1", "Описание задачи1",
                task1.getId(), Status.NEW);
        taskManager.updateTask(updateTask1);
        assertEquals("НоваяЗадача1", taskManager.getTaskById(1).getName());
    }

    @Test
    void updateTask_changeStatus() {
        Task task1 = taskManager.putNewTask(buildTask());
        assertEquals(Status.NEW, task1.getStatus());
        Task updateTask1 = new Task("НоваяЗадача1", "Описание задачи1",
                task1.getId(), Status.IN_PROGRESS);
        taskManager.updateTask(updateTask1);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(1).getStatus());
    }


    @Test
    void updateSubtask_changeEpicStatus_anySubtaskStatusInProgress() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtask(epic));
        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        taskManager.updateSubtask(new Subtask("подзадача 2 обновлена", "Тема подзадачи 2 обновлена", subtask1.getId(), Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(2).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void updateSubtask_changeEpicStatusToDone_allSubtasksStatusDone() {
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
    void putNewTask_UniqueTasks() {
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
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        assertEquals(task1, taskManager.getHistory().get(0));
        assertEquals(task2, taskManager.getHistory().get(1));

    }

    @Test
    void getHistory_returnHistory_withEpics() {
        Epic epic1 = taskManager.putNewEpic(buildEpic());
        Epic epic2 = taskManager.putNewEpic(buildEpic());
        taskManager.getEpicById(1);
        taskManager.getEpicById(2);
        assertEquals(epic1, taskManager.getHistory().get(0));
        assertEquals(epic2, taskManager.getHistory().get(1));
    }

    @Test
    void getHistory_returnHistory_withSubtasks() {
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
    void getHistory_addToHistory_whenGetById() {
        Task task = taskManager.putNewTask(buildTask());
        assertEquals(1, task.getId());
        taskManager.getTaskById(1);
        Epic epic = taskManager.putNewEpic(buildEpic());
        assertEquals(2, epic.getId());
        taskManager.getEpicById(2);
        Subtask subtask = taskManager.putNewSubtask(buildSubtask(epic));
        assertEquals(3, subtask.getId());
        taskManager.getSubtaskById(3);
        assertTrue(taskManager.getHistory().contains(task));
        assertTrue(taskManager.getHistory().contains(epic));
        assertTrue(taskManager.getHistory().contains(subtask));
    }

    @Test //новый тест
    void getHistory_addToHistory_whenGetSameTask() {
        Task task1 = taskManager.putNewTask(buildTask());
        Task task2 = taskManager.putNewTask(buildTask());
        Task task3 = taskManager.putNewTask(buildTask());
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getTaskById(3);
        taskManager.getTaskById(1);
        assertEquals(3, taskManager.getHistory().size());
        assertEquals(task2, taskManager.getHistory().get(0));
        assertEquals(task3, taskManager.getHistory().get(1));
        assertEquals(task1, taskManager.getHistory().get(2));
    }

    @Test
    void putNewSubtask_notAddSubtask_withIdOfEpic() { // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask = taskManager.putNewSubtask(buildSubtask(epic));
        assertNotEquals(epic.getId(), subtask.getId());
    }

    @Test // новый тест
    void deleteSubtaskById_EpicSubtaskListIsEmpty_whenDeleteSubtask(){
        Epic epic = taskManager.putNewEpic(buildEpic());//id 1
        Subtask subtask = taskManager.putNewSubtask(buildSubtask(epic)); // id 2
        taskManager.deleteSubtaskById(2);
        assertEquals(0, epic.getSubtasksId().size() );
    }
}
