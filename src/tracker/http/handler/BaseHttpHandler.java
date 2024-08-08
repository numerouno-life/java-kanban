package tracker.http.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    protected Integer getIdFromPath(String path) {
        String[] pathSplit = path.split("/");
        if (pathSplit.length >= 3) {
            return Integer.parseInt(pathSplit[2]);
        }
        return null;
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Task not found";
        exchange.sendResponseHeaders(404, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String response = "Task overlaps with existing tasks";
        exchange.sendResponseHeaders(406, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }

    protected void sendError(HttpExchange exchange) throws IOException {
        String response = "Internal server error";
        exchange.sendResponseHeaders(500, response.getBytes(StandardCharsets.UTF_8).length);
        exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
        exchange.close();
    }
}
