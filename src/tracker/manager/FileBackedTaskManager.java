package tracker.manager;

import tracker.taskdata.Epic;
import tracker.taskdata.Subtask;
import tracker.taskdata.Task;
import tracker.taskdata.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final Path file;

    public FileBackedTaskManager(File file) {
        this.file = file.toPath();
    }

    private void save() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.toFile()))) {
            bw.append(getHeader());
            bw.newLine();

            for (Task task : getTasks()) {
                bw.append(toString(task)).append("\n");
            }
            for (Epic epic : getEpics()) {
                bw.append(toString(epic)).append("\n");
            }
            for (Subtask subtask : getSubtasks()) {
                bw.append(toString(subtask)).append("\n");
            }
            bw.flush();
        } catch (IOException e) {
            throw ManagerSaveException.saveException(e);
        }

    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        sb.append(getTaskType(task)).append(",");
        sb.append(task.getTitle()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicId());
        }
        return sb.toString();
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TypeOfTask type = TypeOfTask.valueOf(fields[1]);
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK:
                Task task = new Task(title, description, status);
                task.setId(id);
                return task;
            case EPIC:
                Epic epic = new Epic(title, description, status);
                epic.setId(id);
                return epic;
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                Subtask subtask = new Subtask(title, description, status, epicId);
                subtask.setId(id);
                return subtask;
            default:
                throw new IllegalArgumentException("Unknown task type " + type);
        }
    }

    private TypeOfTask getTaskType(Task task) {
        if (task instanceof Subtask) {
            return TypeOfTask.SUBTASK;
        } else if (task instanceof Epic) {
            return TypeOfTask.EPIC;
        } else {
            return TypeOfTask.TASK;
        }
    }

    private static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());

            if (lines.isEmpty()) {
                return fileBackedTaskManager;
            }

            int maxId = 0;
            for (int i = 1; i < lines.size(); i++) {
                Task task = fromString(lines.get(i));
                int taskId = task.getId();
                maxId = Math.max(maxId, taskId);

                if (task instanceof Epic) {
                    fileBackedTaskManager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    fileBackedTaskManager.addSubtask((Subtask) task);
                } else {
                    fileBackedTaskManager.addTask(task);
                }
            }
            fileBackedTaskManager.setCurrentId(maxId);

        } catch (IOException e) {
            throw ManagerSaveException.loadExceptionException(e);
        }
        return fileBackedTaskManager;
    }

    public void loadFromFilePublic(File file) {
        loadFromFile(file);
    }

    public static String getHeader() {
        return "id,type,title,status,description,epic";
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

}
