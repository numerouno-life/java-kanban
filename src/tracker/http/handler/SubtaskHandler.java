package tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import tracker.http.HttpTaskServer;
import tracker.manager.TaskManager;
import tracker.taskdata.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
            List<Subtask> subtasks = taskManager.getSubtasks();
            String response = HttpTaskServer.getGson().toJson(subtasks);
            sendText(exchange, response, 200);
        } else {
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null) {
                String response = HttpTaskServer.getGson().toJson(subtask);
                sendText(exchange, response, 200);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange, Integer id) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = HttpTaskServer.getGson().fromJson(body, Subtask.class);

        try {
            if (id == null) {
                taskManager.createSubtask(subtask);
                sendText(exchange, "", 201);
            } else {
                taskManager.updateSubtask(subtask);
                sendText(exchange, "", 201);
            }
        } catch (IllegalStateException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Integer id) throws IOException {
        try {
            if (id != null) {
                taskManager.deleteSubtaskById(id);
                sendText(exchange, "", 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (IllegalStateException e) {
            sendNotFound(exchange);
        }
    }

}
