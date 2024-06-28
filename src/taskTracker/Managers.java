package taskTracker;

import taskTracker.manager.HistoryManager;
import taskTracker.manager.InMemoryHistoryManager;
import taskTracker.manager.InMemoryTaskManager;
import taskTracker.manager.TaskManager;

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
