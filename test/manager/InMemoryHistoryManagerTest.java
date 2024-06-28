package manager;

import taskTracker.Managers;
import taskTracker.manager.HistoryManager;
import taskTracker.manager.TaskManager;
import taskTracker.taskData.Task;
import taskTracker.taskData.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void add() {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());

        int historySize = historyManager.getHistory().size();
        assertEquals(1, historySize, "History size = 1");

        task1.setDescription("Update Description");
        taskManager.updateTask(task1);

        int updateHistorySize = historyManager.getHistory().size();
        assertEquals(1, updateHistorySize, "History size = 1 after update without viewing");

        taskManager.getTaskById(task1.getId());

        updateHistorySize = historyManager.getHistory().size();
        assertEquals(1, updateHistorySize, "History size = 2 after viewing updated task");

        Task lastVersionHistory = historyManager.getHistory().get(0);
        assertEquals("Update Description", lastVersionHistory.getDescription(), "should be 'Update Description'");
    }
}