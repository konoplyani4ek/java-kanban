import javaKanban.entity.*;
import javaKanban.manager.Managers;
import javaKanban.manager.history.HistoryManager;
import javaKanban.manager.task.InMemoryTaskManager;
import javaKanban.manager.task.TaskManager;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest { //  не исправил все замечания, т.к. уперся в вопрос ниже

    private static TaskManager taskManager = InMemoryTaskManager.getInstance(); // не через менеджер потому что тестируется только класс, а не менеджер

    static Task buildTask() {
        return new Task("task", "description"); // и как для каждого теста делать пустые мапы, потому что  private long taskIdCounter = 1; не сбрасывается до 1? и тесты падают
    }

    static Epic buildEpic() {
        return new Epic("epic", "description");
    }

    static Subtask buildSubtask(Epic epic) {
        return new Subtask("subtask", "description", epic.getId());
    }

    static Task task1; // не понимаю, куда убрать статические переменные, ведь мне надо их использовать в разных тестах?
    static Task task2;
    static Epic epic;

    static Subtask subtask1;
    static Subtask subtask2;


    @Test
    void putNewTask_taskHashMapNotEmpty() {
        task1 = buildTask();
        task2 = buildTask();
        taskManager.putNewTask(task1);
        taskManager.putNewTask(task2);
        assertFalse(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void putNewEpic_epicHashMapNotEmpty() {
        epic = buildEpic();
        taskManager.putNewEpic(epic);
        assertFalse(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void putNewSubtask_subtaskHashMapNotEmpty() {
        subtask1 = buildSubtask(epic);
        subtask2 = buildSubtask(epic);
        taskManager.putNewSubtask(subtask1);
        taskManager.putNewSubtask(subtask2);
        assertFalse(taskManager.getAllSubtasks().isEmpty());
    }

    @AfterAll
    static void clearAllMaps() {
        taskManager.clearAllTasks();
        taskManager.clearAllEpics();
        taskManager.clearAllSubtasks();

        assertEquals(0, taskManager.getAllTasks().size());
        assertEquals(0, taskManager.getAllEpics().size());
        assertEquals(0, taskManager.getAllSubtasks().size());

    }

    @Test
    void getById_ReturnObject() { // не понимаю как объявить переменные на уровне метода и не дублировать создание для каждого теста + сбрасывать счетчик id
        assertEquals(1, task1.getId());
        assertEquals(2, task2.getId());
        assertEquals(3, epic.getId());
    }

    @Test
    void updateTask_ChangeNameValue() {
        Task updateTask1 = new Task("НоваяЗадача1", "Описание задачи1",
                task1.getId(), Status.IN_PROGRESS);
        taskManager.updateTask(updateTask1);
        List<Task> tasks = taskManager.getAllTasks();
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals("НоваяЗадача1", tasks.getFirst().getName());
    }

    @Test
    void updateSubtask_ChangeEpicStatus_NewStatusForSubtask() {
        assertEquals(Status.NEW, epic.getStatus());
        taskManager.updateSubtask(new Subtask("подзадача 2 обновлена", "Тема подзадачи 2 обновлена", 4, Status.IN_PROGRESS, epic.getId()));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

//    @Test
//    void equals_ReturnTrue_SameValues() {
//        Task task3 = new Task("name", "description");
//        Task task4 = new Task("name", "description");
//        taskManager.putNewTask(task3);
//        taskManager.putNewTask(task4);
//        assertNotEquals(task3, task4);
//        ArrayList<Task> tasks = taskManager.getAllTasks();
//        assertTrue(tasks.contains(task3));
//        assertTrue(tasks.contains(task4));
//        assertEquals(6, task3.getId());
//        assertEquals(7, task4.getId());
//    }

    @Test
    void getHistory_Tasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        assertArrayEquals(tasks.toArray(), taskManager.getHistory().toArray());
    }

    @Test
    void getHistory_Epic() {

    }

    @Test
    void getHistory_Subtasks() {

    }
//    @Test
//    void Epic_shouldNotBeAddedToItself() { // проверьте, что объект Epic нельзя добавить в самого себя в виде подзадачи;
//        Epic epic2 = new Epic("e1", "disc1");
//        taskManager.putNewEpic(epic);  // id=1
//        assertEquals(6, epic.getId());
//        Subtask subtask = new Subtask("s1", "disc1", 1);
//        assertNull(subtask.getId());
//        assertThrows(RuntimeException.class, () -> {taskManager.putNewSubtask(subtask);}); // запретил добавление самого себя на уровне метода putNewSubtask
//    }
//    @Test
//    void Subtask_shouldNotBeAddedToItself() {//проверьте, что объект Subtask нельзя сделать своим же эпиком;
//
//    }                                   // не понимаю как сделать. через определенные конструкторы это же можно сделать
}
