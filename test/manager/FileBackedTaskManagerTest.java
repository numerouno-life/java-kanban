package manager;

import org.junit.jupiter.api.Test;
import tracker.manager.FileBackedTaskManager;
import tracker.taskdata.Epic;
import tracker.taskdata.Subtask;
import tracker.taskdata.Task;
import tracker.taskdata.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    // проверка на сохранение и загрузку пустого файла FileBackedTaskManager
    @Test
    public void testCheckForSavingAndLoadingAnEmptyFile() {
        try {
            File tempFile = File.createTempFile("test", ".txt");
            tempFile.deleteOnExit();

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
            fileBackedTaskManager.deleteAllTasks();

            FileBackedTaskManager loadManager = new FileBackedTaskManager(tempFile);
            assertTrue(loadManager.getTasks().isEmpty());
            assertTrue(loadManager.getEpics().isEmpty());
            assertTrue(loadManager.getSubtasks().isEmpty());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // сохранение и загрузку нескольких задач
    @Test
    public void testLoadingMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Task Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Task Description 2", TaskStatus.IN_PROGRESS);
        manager.createTask(task1);
        manager.createTask(task2);

        Epic epic1 = new Epic("Epic 1", "Epic Description 1", TaskStatus.NEW);
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", TaskStatus.IN_PROGRESS, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.deleteAllTasks();

        // Используем публичный метод для загрузки данных
        manager.loadFromFilePublic(tempFile);

        assertEquals(0, manager.getTasks().size(), "Number of tasks should be zero");
        assertEquals(1, manager.getEpics().size(), "Number of epics should match");
        assertEquals(2, manager.getSubtasks().size(), "Number of subtasks should match");

        // Проверяем корректность загруженных данных
        assertEquals(epic1, manager.getEpicById(epic1.getId()), "Epic should match");
        assertEquals(subtask1, manager.getSubtaskById(subtask1.getId()), "Subtask 1 should match");
        assertEquals(subtask2, manager.getSubtaskById(subtask2.getId()), "Subtask 2 should match");
    }

}
