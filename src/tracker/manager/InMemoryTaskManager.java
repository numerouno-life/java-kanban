package tracker.manager;

import tracker.Managers;
import tracker.taskdata.Epic;
import tracker.taskdata.Subtask;
import tracker.taskdata.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    protected TreeSet<Task> sortedTasksByPriority = new TreeSet<>(this::getTaskComparator);

    private int currentId = 1;

    protected int getTaskComparator(Task task1, Task task2) {
        if (task1 == null || task2 == null) {
            return -1;
        }
        if (task1.getStartTime() != null && task2.getStartTime() != null) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() != null) {
            return -1;
        } else if (task2.getStartTime() != null) {
            return 1;
        } else {
            return task1.getId() - task2.getId();
        }
    }

    private boolean isOverlapping(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null || task1.getEndTime() == null || task2.getEndTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) && task1.getEndTime().isAfter(task2.getStartTime());
    }

    private boolean isOverlappingWithExistingTasks(Task task) {
        return sortedTasksByPriority.stream().anyMatch(existingTask -> isOverlapping(task, existingTask) && task.getId() != existingTask.getId());
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
        sortedTasksByPriority.removeIf(task -> task instanceof Task);
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.deleteAllSubtasks();
            epic.updateEpicFields();
        });
        sortedTasksByPriority.removeIf(task -> task instanceof Subtask);
    }

    @Override
    public void deleteAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subtasks.keySet().forEach(historyManager::remove);
        subtasks.clear();
        epics.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public void createTask(Task task) {
        if (isOverlappingWithExistingTasks(task)) {
            throw new IllegalStateException("Task overlaps with existing tasks");
        }
        task.setId(currentId++);
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            sortedTasksByPriority.add(task);
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (isOverlappingWithExistingTasks(subtask)) {
            throw new IllegalStateException("Subtask overlaps with existing tasks");
        }
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtask.setId(currentId++);
            subtasks.put(subtask.getId(), subtask);
            epic.addSubtask(subtask);
            epic.updateEpicFields();
            if (subtask.getStartTime() != null) {
                sortedTasksByPriority.add(subtask);
            }
        }
    }


    @Override
    public void createEpic(Epic epic) {
        epic.updateEpicFields();
        epic.setId(currentId++);
        epics.put(epic.getId(), epic);
    }

    protected void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    protected void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateTask(Task task) {
        if (isOverlappingWithExistingTasks(task)) {
            throw new IllegalStateException("Task overlaps with existing tasks");
        }
        tasks.replace(task.getId(), task);
        sortedTasksByPriority.remove(task);
        if (task.getStartTime() != null) {
            sortedTasksByPriority.add(task);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isOverlappingWithExistingTasks(subtask)) {
            throw new IllegalStateException("Subtask overlaps with existing tasks");
        }
        Subtask existSubtask = subtasks.get(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null && existSubtask != null && existSubtask.getEpicId() == epic.getId()) {
            subtasks.replace(subtask.getId(), subtask);
            sortedTasksByPriority.remove(subtask);
            if (subtask.getStartTime() != null) {
                sortedTasksByPriority.add(subtask);
            }
            epic.updateEpicFields();
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic existEpic = epics.get(epic.getId());
        if (existEpic != null) {
            existEpic.setTitle(epic.getTitle());
            existEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteTaskById(int id) {
        historyManager.remove(id);
        Task task = tasks.remove(id);
        if (task != null) {
            sortedTasksByPriority.remove(task);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        historyManager.remove(id);
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            sortedTasksByPriority.remove(subtask);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtaskById(subtask.getId());
                epic.updateEpicFields();
            }
        }
    }

    @Override
    public void deleteEpicById(int id) {
        historyManager.remove(id);
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks().values()) {
                historyManager.remove(subtask.getId());
                subtasks.remove(subtask.getId());
                sortedTasksByPriority.remove(subtask);
            }
        }
    }

    @Override
    public List<Subtask> getAllSubtaskByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return new ArrayList<>(epic.getSubtasks().values());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(sortedTasksByPriority);
    }

    protected void setCurrentId(int id) {
        this.currentId = id;
    }
}
