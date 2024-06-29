package tasktracker;

import tasktracker.manager.HistoryManager;
import tasktracker.manager.InMemoryHistoryManager;
import tasktracker.manager.InMemoryTaskManager;
import tasktracker.manager.TaskManager;

public class Managers {
    private static HistoryManager historyManager;

    public static TaskManager getDefault() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
}
