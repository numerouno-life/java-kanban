package tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import tracker.http.HttpTaskServer;
import tracker.manager.TaskManager;
import tracker.taskdata.Task;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handleGet(exchange);
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

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        if (prioritizedTasks.isEmpty()) {
            sendText(exchange, "List prioritizedTasks is Empty ", 404);
        } else {
            String response = HttpTaskServer.getGson().toJson(prioritizedTasks);
            sendText(exchange, response, 200);
        }
    }
}
