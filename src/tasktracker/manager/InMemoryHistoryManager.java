package tasktracker.manager;

import tasktracker.taskdata.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private Map<Integer,Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            remove(task.getId());
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = first;
        while (current != null) {
            history.add(current.value);
            current = current.next;
        }
        return history;
    }

    // Добавляет задачу в конец связного списка. Обновляет ссылки в предыдущем последнем узле и добавляет новый узел в nodeMap
    private void linkLast(Task task) {
        Node newNode = new Node(last, null, task);
        if (last != null) {
            last.next = newNode;
        }
        last = newNode;
        if (first == null) {
            first = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    // Удаляет узел из связного списка, обновляя ссылки на соседние узлы.
    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev == null) {
            first = node.next;
            if (first != null) {
                first.prev = null;
            } else {
                last = null;
            }
        } else if (node.next == null) {
            last = node.prev;
            last.next = null;
        } else {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }
        node.prev = null;
        node.next = null;
    }

    private static class Node {
        Node prev;
        Node next;
        Task value;

        public Node(Node prev, Node next, Task value) {
            this.prev = prev;
            this.next = next;
            this.value = value;
        }
    }
}
