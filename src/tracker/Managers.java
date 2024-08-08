package tracker;

import tracker.manager.HistoryManager;
import tracker.manager.InMemoryHistoryManager;
import tracker.manager.InMemoryTaskManager;
import tracker.manager.TaskManager;

public class Managers {
    private static HistoryManager historyManager;

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        if (historyManager == null) {
            historyManager = new InMemoryHistoryManager();
        }
        return historyManager;
    }
}
