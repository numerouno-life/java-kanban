package tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import tracker.http.HttpTaskServer;
import tracker.manager.TaskManager;
import tracker.taskdata.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            Integer id = getIdFromPath(path);

            switch (method) {
                case "GET":
                    handleGet(exchange, id);
                    break;
                case "POST":
                    handlePost(exchange, id);
                    break;
                case "DELETE":
                    handleDelete(exchange, id);
                    break;
                default:
                    sendError(exchange);
                    break;
            }
        } catch (Exception e) {
            sendError(exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleGet(HttpExchange exchange, Integer id) throws IOException {
        if (id == null) {
            List<Task> tasks = taskManager.getTasks();
            String response = HttpTaskServer.getGson().toJson(tasks);
            sendText(exchange, response, 200);
        } else {
            Task task = taskManager.getTaskById(id);
            if (task != null) {
                String response = HttpTaskServer.getGson().toJson(task);
                sendText(exchange, response, 200);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange, Integer id) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = HttpTaskServer.getGson().fromJson(body, Task.class);

        try {
            if (id == null) {
                taskManager.createTask(task);
                sendText(exchange, "Task created with id: " + taskManager.getTaskById(task.getId()) + ".", 201);
            } else {
                taskManager.updateTask(task);
                sendText(exchange, "Task with id: " + taskManager.getTaskById(task.getId()) + " is update.", 201);
            }
        } catch (IllegalStateException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Integer id) throws IOException {
        if (id != null) {
            try {
                taskManager.deleteTaskById(id);
                sendText(exchange, "Task deleted!", 200);
            } catch (IllegalStateException e) {
                sendNotFound(exchange);
            }
        } else {
            sendNotFound(exchange);
        }
    }
}
