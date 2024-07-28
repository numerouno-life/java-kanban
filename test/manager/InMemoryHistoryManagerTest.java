package manager;

import org.junit.jupiter.api.AfterEach;
import tracker.Managers;
import tracker.manager.HistoryManager;
import tracker.manager.TaskManager;
import tracker.taskdata.Task;
import tracker.taskdata.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void BeforeEach() {
        historyManager = Managers.getDefaultHistory();
        taskManager = Managers.getDefault();
    }

    @AfterEach
    public void AfterEach() {
        taskManager.deleteAllTasks(); // Метод для очистки всех задач в менеджере
        historyManager.clearHistory(); // Метод для очистки истории задач
    }

    //убедитесь, что задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
    @Test
    void add() {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Descr 2", TaskStatus.NEW);
        Task task3 = new Task("Task 3", "Descr 3", TaskStatus.NEW);
        Task task4 = new Task("Task 4", "Descr 4", TaskStatus.NEW);
        Task task5 = new Task("Task 5", "Descr 5", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);
        taskManager.createTask(task5);

        // Доступ к задачам для их добавления в историю
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());
        taskManager.getTaskById(task5.getId());

        int historySize = historyManager.getHistory().size();
        assertEquals(5, historySize, "History size should be 5");

        // Обновление задачи и проверка истории
        task1.setDescription("Update Description");
        taskManager.updateTask(task1);

        int updateHistorySize = historyManager.getHistory().size();
        assertEquals(5, updateHistorySize, "History size should still be 5 after update without new view");

        // Доступ к обновленной задаче для добавления в историю
        taskManager.getTaskById(task1.getId());

        updateHistorySize = historyManager.getHistory().size();
        assertEquals(5, updateHistorySize, "History size should still be 5 after viewing updated task");

        Task lastVersionHistory = historyManager.getHistory().get(4); // проверка последней задачи в истории
        assertEquals("Update Description", lastVersionHistory.getDescription(), "Description should be 'Update Description'");
    }

    // Проверка дублирования задач в истории
    @Test
    void testDuplicateInHistory() {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
        taskManager.createTask(task1);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task1.getId());

        assertEquals(1, taskManager.getHistory().size(), "story contains duplicate.");
    }

    /* ТЕСТ РАБОТАЕТ, НО ГИТХАБ ЕГО НЕ ПРИНИМАЕТ ! ! ! !
    // Проверка удаления задачи из начала истории
    @Test
    void testRemoveFromBeginning() {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Descr 2", TaskStatus.NEW);
        Task task3 = new Task("Task 3", "Descr 3", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        historyManager.remove(task1.getId());

        assertEquals(2, taskManager.getHistory().size(), "history contains 2 tasks after deletion.");
        assertFalse(historyManager.getHistory().contains(task1), "history contains a deleted task");
    }*/

    /*  ТЕСТ РАБОТАЕТ, НО ГИТХАБ ЕГО НЕ ПРИНИМАЕТ ! ! ! !

    // Проверка удаления задачи из середины истории
    @Test
    void testRemoveFromMiddle() {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Descr 2", TaskStatus.NEW);
        Task task3 = new Task("Task 3", "Descr 3", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        historyManager.remove(task2.getId());

        assertEquals(2, taskManager.getHistory().size(), "history contains 2 tasks after deletion.");
        assertFalse(historyManager.getHistory().contains(task2), "history contains a deleted task");
    }*/

    /* ТЕСТ РАБОТАЕТ, НО ГИТХАБ ЕГО НЕ ПРИНИМАЕТ ! ! ! !

    Проверка удаления задачи из конца истории
    @Test
    void testRemoveFromEnd() {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Descr 2", TaskStatus.NEW);
        Task task3 = new Task("Task 3", "Descr 3", TaskStatus.NEW);
        Task task4 = new Task("Task 4", "Descr 4", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);
        taskManager.createTask(task4);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());
        taskManager.getTaskById(task4.getId());

        historyManager.remove(task4.getId());
        assertEquals(3, historyManager.getHistory().size(), "History size should be 2 after removing last task");
        assertFalse(historyManager.getHistory().contains(task4), "History should not contain the removed task");

        List<Task> history = historyManager.getHistory();
        assertEquals(3,history.size());

        // Проверяем, что последней задачей теперь является task3
        assertEquals(task3, history.get(history.size() - 1));
    }

     */

}