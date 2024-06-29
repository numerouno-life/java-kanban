package tasktracker.manager;

import tasktracker.taskdata.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);
    void remove(int id);

    List<Task> getHistory();

}
