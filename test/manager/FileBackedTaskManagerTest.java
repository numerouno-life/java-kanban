package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.Managers;
import tracker.manager.FileBackedTaskManager;
import tracker.taskdata.Epic;
import tracker.taskdata.Subtask;
import tracker.taskdata.Task;
import tracker.taskdata.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    // проверка на сохранение и загрузку пустого файла FileBackedTaskManager
    @Test
    public void testCheckForSavingAndLoadingAnEmptyFile() {
        try {
            File tempFile = File.createTempFile("test", ".txt");
            tempFile.deleteOnExit();

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
            fileBackedTaskManager.save();

            FileBackedTaskManager loadManager = FileBackedTaskManager.loadFromFile(tempFile);
            assertTrue(loadManager.getTasks().isEmpty());
            assertTrue(loadManager.getEpics().isEmpty());
            assertTrue(loadManager.getSubtasks().isEmpty());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // сохранение нескольких задач
    @Test
    public void testSavingMultipleTasks() {
        try {
            File tempFile = File.createTempFile("test", ".txt");
            tempFile.deleteOnExit();

            FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);
            Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
            Epic epic1 = new Epic("Epic 1", "Descr epic 1", TaskStatus.NEW);
            Subtask subtask2 = new Subtask("Subtask 2", "Descr 2", TaskStatus.IN_PROGRESS, epic1.getId());

            fileBackedTaskManager.createTask(task1);
            fileBackedTaskManager.createTask(epic1);
            fileBackedTaskManager.createTask(subtask2);

            List<String> lines = Files.readAllLines(tempFile.toPath());
            assertTrue(lines.size() > 1);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // сохранение и загрузку нескольких задач
    @Test
    public void testLoadingMultipleTasks() throws IOException {
        // Создаем временный файл
        File tempFile = File.createTempFile("test", ".txt");
        tempFile.deleteOnExit();

        // Записываем в файл строки, представляющие задачи
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            writer.write("id,type,name,status,description,epic\n"); // Заголовок CSV
            writer.write("1,TASK,Task 1,NEW,Description 1,\n");
            writer.write("2,TASK,Task 2,DONE,Description 2,\n");
            writer.write("3,TASK,Task 3,DONE,Description 3,\n");
            writer.write("4,EPIC,Epic 4,NEW,Description 4,\n");
            writer.write("5,SUBTASK,Subtask 5,NEW,Description 5,4\n");
            writer.write("6,EPIC,Epic 6,NEW,Description 6,\n");
        }

        // Загружаем данные из файла в новый экземпляр менеджера задач
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что задачи загружены корректно
        assertEquals(3, loadedManager.getTasks().size(), "tasks size mismatch");
        assertEquals(2, loadedManager.getEpics().size(), "epics size mismatch");
        assertEquals(1, loadedManager.getSubtasks().size(), "subtasks size mismatch");

        // Проверяем загруженные задачи
        Task loadedTask1 = loadedManager.getTaskById(1);
        assertNotNull(loadedTask1, "Task 1 should not be null");
        assertEquals("Task 1", loadedTask1.getTitle());
        assertEquals("Description 1", loadedTask1.getDescription());
        assertEquals(TaskStatus.NEW, loadedTask1.getStatus());

        Task loadedTask2 = loadedManager.getTaskById(2);
        assertNotNull(loadedTask2, "Task 2 should not be null");
        assertEquals("Task 2", loadedTask2.getTitle());
        assertEquals("Description 2", loadedTask2.getDescription());
        assertEquals(TaskStatus.DONE, loadedTask2.getStatus());

        Task loadedTask3 = loadedManager.getTaskById(3);
        assertNotNull(loadedTask3, "Task 3 should not be null");
        assertEquals("Task 3", loadedTask3.getTitle());
        assertEquals("Description 3", loadedTask3.getDescription());
        assertEquals(TaskStatus.DONE, loadedTask3.getStatus());

        Epic loadedEpic4 = loadedManager.getEpicById(4);
        assertNotNull(loadedEpic4, "Epic 4 should not be null");
        assertEquals("Epic 4", loadedEpic4.getTitle());
        assertEquals("Description 4", loadedEpic4.getDescription());
        assertEquals(TaskStatus.NEW, loadedEpic4.getStatus());

        Subtask loadedSubtask5 = loadedManager.getSubtaskById(5);
        assertNotNull(loadedSubtask5, "Subtask 5 should not be null");
        assertEquals("Subtask 5", loadedSubtask5.getTitle());
        assertEquals("Description 5", loadedSubtask5.getDescription());
        assertEquals(TaskStatus.NEW, loadedSubtask5.getStatus());
        assertEquals(4, loadedSubtask5.getEpicId());

        Epic loadedEpic6 = loadedManager.getEpicById(6);
        assertNotNull(loadedEpic6, "Epic 6 should not be null");
        assertEquals("Epic 6", loadedEpic6.getTitle());
        assertEquals("Description 6", loadedEpic6.getDescription());
        assertEquals(TaskStatus.NEW, loadedEpic6.getStatus());
    }

}
