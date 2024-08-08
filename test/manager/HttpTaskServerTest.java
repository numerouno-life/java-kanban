package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tracker.http.HttpTaskServer;
import tracker.manager.InMemoryTaskManager;
import tracker.manager.TaskManager;
import tracker.taskdata.Epic;
import tracker.taskdata.Subtask;
import tracker.taskdata.Task;
import tracker.taskdata.TaskStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskServerTest() throws IOException {
    }


    @BeforeEach
    public void setUp() {
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubtasks();
        taskManager.deleteAllEpics();
        httpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        httpTaskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test 2", "Testing task 2", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTasks();

        assertNotNull(tasksFromManager, "Tasks are not returned");
        assertEquals(1, tasksFromManager.size(), "Incorrect number of tasks");
        assertEquals("Test 2", tasksFromManager.get(0).getTitle(), "Incorrect name of task");

    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test 2", "Description", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());

        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtasksFromManager = taskManager.getSubtasks();

        assertNotNull(subtasksFromManager, "Subtasks are not returned");
        assertEquals(1, subtasksFromManager.size(), "Incorrect number of subtasks");
        assertEquals("Test 2", subtasksFromManager.get(0).getTitle(), "Incorrect name of subtask");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test 2", "Testing epic 2", TaskStatus.NEW);
        Subtask subtask = new Subtask("Test 2", "Description", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.updateEpic(epic);

        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicFromManager = taskManager.getEpics();

        assertNotNull(epicFromManager, "Epics are not returned");
        assertEquals(1, epicFromManager.size(), "Incorrect number of epics");
        assertEquals("Test 2", epicFromManager.get(0).getTitle(), "Incorrect name of epic");
    }

    @Test
    public void testTaskUpdate() throws IOException, InterruptedException {
        Task task = new Task("Task", "Descr", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createTask(task);

        task.setTitle("Updated Task");
        task.setDescription("Updated Description");
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем статус код и обновленные данные
        assertEquals(201, response.statusCode());
        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task", updatedTask.getTitle(), "Task title was not updated");
        assertEquals("Updated Description", updatedTask.getDescription(), "Task description was not updated");
    }

    @Test
    public void testSubtaskUpdate() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Descr", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);

        subtask.setTitle("Updated Subtask");
        subtask.setDescription("Updated Description");
        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        Subtask updatedSubtask = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(updatedSubtask, "Updated subtask should not be null");
        assertEquals("Updated Subtask", updatedSubtask.getTitle(), "Subtask title was not updated");
        assertEquals("Updated Description", updatedSubtask.getDescription(), "Subtask description was not updated");
    }

    @Test
    public void testEpicUpdate() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "Descr", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);

        epic.setTitle("Updated Subtask");
        epic.setDescription("Updated Description");
        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(updatedEpic, "Updated Epic should not be null");
        assertEquals("Updated Subtask", updatedEpic.getTitle(), "Epic title was not updated");
        assertEquals("Updated Description", updatedEpic.getDescription(), "Epic description was not updated");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "taskGET", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String taskJson = gson.toJson(task);
        assertEquals(taskJson, response.body());
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Subtask", "SubtaskGET", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), epic.getId());
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String subtaskJson = gson.toJson(subtask);
        assertEquals(subtaskJson, response.body());
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "EpicGET", TaskStatus.NEW);
        taskManager.createTask(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String epicJson = gson.toJson(epic);
        assertEquals(epicJson, response.body());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task to Delete", "Description", TaskStatus.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        taskManager.createTask(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + task.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task deletedTask = taskManager.getTaskById(task.getId());
        assertNull(deletedTask, "Task was not deleted");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Subtask subtask = new Subtask("Subtask", "SubtaskGET", TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now(), 1);
        taskManager.createSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask deletedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNull(deletedSubtask, "This task was Deleted");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic", "EpicGET", TaskStatus.NEW);
        taskManager.createTask(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic deletedEpic = taskManager.getEpicById(epic.getId());
        assertNull(deletedEpic, "This task was Deleted");
    }

    @Test
    public void testHistoryGet() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Descr 2", TaskStatus.NEW);
        Task task3 = new Task("Task 3", "Descr 3", TaskStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task3.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> historyFromServer = gson.fromJson(response.body(), taskListType);

        assertEquals(3, historyFromServer.size());
        assertEquals(task1.getId(), historyFromServer.get(0).getId());
        assertEquals(task2.getId(), historyFromServer.get(1).getId());
        assertEquals(task3.getId(), historyFromServer.get(2).getId());

    }

    @Test
    public void testPrioritizedGet() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Descr 1", TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        Task task2 = new Task("Task 2", "Descr 2", TaskStatus.NEW, Duration.ofMinutes(45), LocalDateTime.now().plusHours(2));
        Task task3 = new Task("Task 3", "Descr 3", TaskStatus.NEW, Duration.ofMinutes(15), LocalDateTime.now().plusHours(3));

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createTask(task3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type taskListType = new TypeToken<List<Task>>() {
        }.getType();

        List<Task> prioritizedTasks = gson.fromJson(response.body(), taskListType);

        assertEquals(3, prioritizedTasks.size());
        assertEquals(task1.getId(), prioritizedTasks.get(0).getId());
        assertEquals(task2.getId(), prioritizedTasks.get(1).getId());
        assertEquals(task3.getId(), prioritizedTasks.get(2).getId());
    }

}
