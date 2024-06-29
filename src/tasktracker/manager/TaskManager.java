package tasktracker.manager;

import tasktracker.taskdata.Epic;
import tasktracker.taskdata.Subtask;
import tasktracker.taskdata.Task;

import java.util.List;

public interface TaskManager {
    List<Task> getTasks();

    List<Subtask> getSubtasks();

    List<Epic> getEpics();


    void deleteAllTasks();

    void deleteAllSubtasks();

    void deleteAllEpics();


    Task getTaskById(int id);

    Subtask getSubtaskById(int id);

    Epic getEpicById(int id);


    void createTask(Task task);

    void createSubtask(Subtask subtask);

    void createEpic(Epic epic);


    void updateTask(Task task);

    void updateSubtask(Subtask subtask);

    void updateEpic(Epic epic);


    void deleteTaskById(int id);

    void deleteSubtaskById(int id);

    void deleteEpicById(int id);


    List<Subtask> getAllSubtaskByEpicId(int epicId);

    List<Task> getHistory();
}
