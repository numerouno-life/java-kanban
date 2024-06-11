package TaskTracker;

import TaskTracker.manager.HistoryManager;
import TaskTracker.manager.InMemoryHistoryManager;
import TaskTracker.manager.InMemoryTaskManager;
import TaskTracker.manager.TaskManager;

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
