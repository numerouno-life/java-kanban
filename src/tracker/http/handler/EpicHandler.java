package tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import tracker.http.HttpTaskServer;
import tracker.manager.TaskManager;
import tracker.taskdata.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
            List<Epic> epics = taskManager.getEpics();
            String response = HttpTaskServer.getGson().toJson(epics);
            sendText(exchange, response, 200);
        } else {
            Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                String response = HttpTaskServer.getGson().toJson(epic);
                sendText(exchange, response, 200);
            } else {
                sendNotFound(exchange);
            }
        }
    }

    private void handlePost(HttpExchange exchange, Integer id) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = HttpTaskServer.getGson().fromJson(body, Epic.class);

        try {
            if (id == null) {
                taskManager.createEpic(epic);
                sendText(exchange, "Epic created with id: " + taskManager.getEpicById(epic.getId()), 201);
            } else {
                taskManager.updateEpic(epic);
                sendText(exchange, "Epic with id: " + taskManager.getEpicById(epic.getId()) + " update", 201);
            }
        } catch (IllegalStateException e) {
            sendHasInteractions(exchange);
        }
    }

    private void handleDelete(HttpExchange exchange, Integer id) throws IOException {
        try {
            if (id != null) {
                taskManager.deleteEpicById(id);
                sendText(exchange, "Epic deleted", 200);
            } else {
                sendNotFound(exchange);
            }
        } catch (IllegalStateException e) {
            sendNotFound(exchange);
        }
    }
}
