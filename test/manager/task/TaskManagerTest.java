package manager.task;

import javakanban.manager.task.TaskManager;

abstract class TaskManagerTest<T extends TaskManager> {
    // не понимаю какие тесты можно вынести сюда из FileBackedTaskManager
    void putNewSubtask_shouldBeLinkedToExistingEpic() {
    }

    void epicStatus_shouldBeCalculatedBasedOnSubtasks() {
    }

    void tasksShouldNotOverlapInTime_usingBuildTask() {
    }

    void putNewTask_shouldThrowException_whenTasksIntersect() {
    }

}

