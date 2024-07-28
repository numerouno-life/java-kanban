package manager;

import org.junit.jupiter.api.Test;
import tracker.manager.InMemoryTaskManager;
import tracker.manager.TaskManager;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    //убедитесь, что утилитарный класс всегда возвращает проинициализированные и готовые к работе экземпляры менеджеров;
    @Test
    public void testUtilityClassAlwaysReturnsInitializedAndReadyManagers() {
        assertNotNull(taskManager, "Task manager should be initialized.");
        assertInstanceOf(TaskManager.class, taskManager);
    }

}