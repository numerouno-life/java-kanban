package tracker.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.sun.net.httpserver.HttpServer;
import tracker.Managers;
import tracker.http.adapter.DurationAdapter;
import tracker.http.adapter.LocalDateTImeAdapter;
import tracker.http.handler.*;
import tracker.manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;

    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.httpServer.createContext("/tasks", new TaskHandler(taskManager));
        this.httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        this.httpServer.createContext("/epics", new EpicHandler(taskManager));
        this.httpServer.createContext("/history", new HistoryHandler(taskManager));
        this.httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public void start() {
        httpServer.start();
        System.out.println("Server is started on PORT: " + PORT);
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Server is stopped on PORT: " + PORT);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTImeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());

        return gsonBuilder.create();
    }
}
