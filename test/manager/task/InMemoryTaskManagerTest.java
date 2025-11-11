package manager.task;

import javakanban.entity.*;
import javakanban.manager.history.InMemoryHistoryManager;
import javakanban.manager.task.InMemoryTaskManager;
import javakanban.manager.task.TaskManager;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    TaskManager taskManager;

    Task buildTask() {
        Task task = new Task("task", "description");
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.now());
        return task;
    }

    Epic buildEpic() {
        return new Epic("epic", "description"); // новые параметры null
    }

    Subtask buildSubtaskWithTemporalParams(Epic epic) {
        Subtask subtask = new Subtask("subtask", "description", epic.getId());
        subtask.setDuration(Duration.ofMinutes(60));
        subtask.setStartTime(LocalDateTime.now());
        return subtask;
    }

    @BeforeEach
    void initTaskManager() {
        taskManager = new InMemoryTaskManager();

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
    void Epic_equals_returnTrue_SameId() { // перенес из тестов Эпика
        Epic epic1 = taskManager.putNewEpic(buildEpic());
        Epic epic2 = taskManager.putNewEpic(buildEpic());
        epic2.setId(epic1.getId());
        assertEquals(epic1.getId(), epic2.getId());
        assertEquals(epic1, epic2);
    }

    @Test
    void putNewSubtask_subtaskHashMapHasElements() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        assertEquals(1, taskManager.getAllSubtasks().size());
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
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        taskManager.clearAllEpics();
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void clearAllSubtasks_mapIsEmpty_deleteSubtasks() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
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
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        assertEquals("subtask", subtask1.getName());
        assertEquals("description", subtask1.getDescription());
        assertEquals(2, subtask1.getId());
    }

    @Test
    void updateTask_changeNameValue() {
        Task task1 = taskManager.putNewTask(buildTask()); // id = 1
        Task updateTask1 = new Task(1L, "НоваяЗадача1", Status.NEW, "Описание задачи1", Duration.ofMinutes(60), LocalDateTime.now());
        System.out.println(taskManager.getAllTasks());
        taskManager.updateTask(updateTask1);
        assertEquals("НоваяЗадача1", taskManager.getTaskById(1).getName());
    }

    @Test
    void updateTask_changeStatus() {
        Task task1 = taskManager.putNewTask(buildTask());
        assertEquals(Status.NEW, task1.getStatus());
        Task updateTask1 = new Task(1L, "НоваяЗадача1", Status.IN_PROGRESS, "Описание задачи1", Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.updateTask(updateTask1);
        assertEquals(Status.IN_PROGRESS, taskManager.getTaskById(1).getStatus());
    }


    @Test
    void updateSubtask_changeEpicStatus_anySubtaskStatusInProgress() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        assertEquals(Status.NEW, subtask1.getStatus());
        assertEquals(Status.NEW, epic.getStatus());
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "подзадача 2 обновлена", Status.IN_PROGRESS, "Тема подзадачи 2 обновлена", Duration.ofMinutes(60), LocalDateTime.now(), epic.getId()));
        assertEquals(Status.IN_PROGRESS, taskManager.getSubtaskById(2).getStatus());
        assertEquals(Status.IN_PROGRESS, taskManager.getEpicById(1).getStatus());
    }

    @Test
    void updateSubtask_changeEpicStatusToDone_allSubtasksStatusDone() {
        Epic epic = taskManager.putNewEpic(buildEpic());
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        taskManager.updateSubtask(new Subtask(subtask1.getId(), "subtask1", Status.DONE, "subtask1disc", Duration.ofMinutes(60), LocalDateTime.now(), epic.getId()));
        taskManager.updateSubtask(new Subtask(subtask2.getId(), "subtask2", Status.DONE, "subtask2disc", Duration.ofMinutes(60), LocalDateTime.now(), epic.getId()));
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
        Subtask subtask1 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic1));
        Subtask subtask2 = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic2));
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
        Subtask subtask = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        assertEquals(3, subtask.getId());
        taskManager.getSubtaskById(3);
        assertTrue(taskManager.getHistory().contains(task));
        assertTrue(taskManager.getHistory().contains(epic));
        assertTrue(taskManager.getHistory().contains(subtask));
    }

    @Test
        //новый тест
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
        Subtask subtask = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));
        assertNotEquals(epic.getId(), subtask.getId());
    }

    @Test
    void deleteSubtaskById_EpicSubtaskListIsEmpty_whenDeleteSubtask() {
        Epic epic = taskManager.putNewEpic(buildEpic());//id 1
        Subtask subtask = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic)); //id 2
        taskManager.deleteSubtaskById(2);
        assertEquals(0, epic.getSubtasksId().size());
    }

    @Test
    void putNewSubtask_shouldBeLinkedToExistingEpic() {
        // создаем эпик
        Epic epic = new Epic("Основной проект", "Описание эпика");
        taskManager.putNewEpic(epic);

        // добавляем сабтаск
        Subtask subtask = taskManager.putNewSubtask(buildSubtaskWithTemporalParams(epic));

        // проверяем, что сабтаск появился в менеджере
        assertNotNull(taskManager.getSubtaskById(subtask.getId()));
        // и что он связан с эпиком
        Epic linkedEpic = taskManager.getEpicById(epic.getId());
        List<Long> ids = linkedEpic.getSubtasksId();
        assertTrue(ids.contains(subtask.getId()), "Эпик должен содержать ID подзадачи");
    }

    @Test
    void epicStatus_shouldBeCalculatedBasedOnSubtasks() {
        // создаём эпик
        Epic epic = buildEpic();
        taskManager.putNewEpic(epic);

        Subtask subtask1 = buildSubtaskWithTemporalParams(epic);
        Subtask subtask2 = buildSubtaskWithTemporalParams(epic);

        taskManager.putNewSubtask(subtask1);
        taskManager.putNewSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.NEW, updatedEpic.getStatus());

        subtask1.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);

        updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);

        updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus());
    }

    @Test
    void tasksShouldNotOverlapInTime_usingBuildTask() {

        Task task1 = buildTask();
        task1.setStartTime(LocalDateTime.of(2025, 11, 11, 10, 0));
        taskManager.putNewTask(task1);

        // пересекается с первой
        Task overlappingTask = buildTask();
        overlappingTask.setStartTime(LocalDateTime.of(2025, 11, 11, 10, 30));

        assertThrows(IllegalStateException.class,
                () -> taskManager.putNewTask(overlappingTask),
                "Должно выбрасываться исключение при пересечении задач по времени");

        // третья задача без пересечения
        Task nonOverlappingTask = buildTask();
        nonOverlappingTask.setStartTime(LocalDateTime.of(2025, 11, 11, 11, 0));

        assertDoesNotThrow(() -> taskManager.putNewTask(nonOverlappingTask),
                "Непересекающиеся задачи должны добавляться без ошибок");
    }

    @Test
    void putNewTask_shouldThrowException_whenTasksIntersect() {
        // Добавляем первую задачу
        Task task1 = buildTask();
        task1.setStartTime(LocalDateTime.of(2025, 11, 11, 10, 0));
        taskManager.putNewTask(task1);

        //  пересекается по времени с первой
        Task task2 = buildTask();
        task2.setStartTime(LocalDateTime.of(2025, 11, 11, 10, 30));
        // Проверяем, что выбрасывается исключение
        assertThrows(IllegalStateException.class, () -> {
            taskManager.putNewTask(task2);
        }, "Нельзя добавить. Таск пересекается с другим по времени");
    }
}
