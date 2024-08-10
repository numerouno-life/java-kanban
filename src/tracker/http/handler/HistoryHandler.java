package tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import tracker.http.HttpTaskServer;
import tracker.manager.TaskManager;
import tracker.taskdata.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    private TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();

            switch (method) {
                case "GET":
                    handleGet(exchange);
                default:
                    sendError(exchange);
                    break;
            }
        } catch (Exception e) {
            sendError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<Task> history = taskManager.getHistory();
        if (history.isEmpty()) {
            sendText(exchange, "List history is empty ", 404);
        } else {
            String response = HttpTaskServer.getGson().toJson(history);
            sendText(exchange, response, 200);
        }
    }
}
