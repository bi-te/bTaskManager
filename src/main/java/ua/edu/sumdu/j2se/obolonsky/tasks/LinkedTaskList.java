package ua.edu.sumdu.j2se.obolonsky.tasks;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Doubly-linked {@code LinkedTaskList} represents a list of tasks.
 */
public class LinkedTaskList extends AbstractTaskList {
    /**
     * The first node of the list.
     */
    private Node first;

    /**
     * The last node of the list.
     */
    private Node last;

    private int tasks;

    /**
     * Constructor that creates a new empty list.
     */
    public LinkedTaskList() {
    }

    @NotNull
    @Override
    public Iterator<Task> iterator() {
        return new TaskListIterator();
    }

    /**
     * The inner class that represents a node of the list.
     */
    private class Node {
        Node prev;
        Node next;
        Task task;

        Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private class TaskListIterator implements Iterator<Task> {
        private Node cursor;
        private Node next = first;

        TaskListIterator() {
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public Task next() {
            if (!hasNext())
                throw new NoSuchElementException("The iteration has no more elements");

            Task task = next.task;
            cursor = next;
            next = next.next;
            return task;
        }

        @Override
        public void remove() {
            if (cursor == null) {
                throw new IllegalStateException();
            }

            if (cursor.prev == null) {
                first = cursor.next;
            } else {
                cursor.prev.next = cursor.next;
            }

            if (cursor.next == null) {
                last = cursor.prev;
            } else {
                cursor.next.prev = cursor.prev;
            }

            tasks--;
            cursor = null;
        }
    }

    /**
     * Appends the specified element to the end of the list.
     *
     * @param task The new {@code Task} element
     */
    @Override
    public void add(@NotNull Task task) {
        Node newTask = new Node(task, last, null);
        final Node lastNode = last;
        last = newTask;

        if (lastNode == null) {
            first = newTask;
        } else {
            lastNode.next = newTask;
        }
        tasks++;
    }

    /**
     * Removes the given task from the list.
     *
     * @param task The task to be removed
     * @return {@code true} if element is in the list or
     * {@code false} if not
     */
    @Override
    public boolean remove(Task task) {
        if (task == null) {
            return false;
        }

        Node current = first;

        for (; current != null; current = current.next) {
            if (current.task.equals(task)) {

                if (current.prev == null) {
                    first = current.next;
                } else {
                    current.prev.next = current.next;
                }

                if (current.next == null) {
                    last = current.prev;
                } else {
                    current.next.prev = current.prev;
                }
                tasks--;
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the task at the specified position in the list.
     *
     * @param index The position from where task will be taken
     * @return The {@code Task} object
     * @throws IndexOutOfBoundsException if {@code index} is out of range.
     */
    @Override
    public Task getTask(int index) {
        if (index < 0 || index >= tasks) {
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for length " + tasks);
        }

        Node returnNode;
        if (index < tasks / 2) {
            returnNode = first;
            for (int i = 0; i < index; i++) {
                returnNode = returnNode.next;
            }
        } else {
            returnNode = last;
            for (int i = tasks - 1; i > index; i--) {
                returnNode = returnNode.prev;
            }
        }
        return returnNode.task;
    }

    @Override
    public int size() {
        return tasks;
    }

    @Override
    public ListTypes.types getType() {
        return ListTypes.types.LINKED;
    }

    /**
     * Returns a string representation of the contents of the list.
     * The string representation consists of the list's elements,
     * enclosed in square brackets ({@code "[]"}).  Adjacent elements are
     * separated by the characters {@code ", "} (a comma followed by a
     * space).
     *
     * @return A string representation of the list.
     */
    @Override
    public String toString() {
        if (tasks == 0) {
            return "[]";
        }
        StringBuilder ans = new StringBuilder("[");
        int i = 0;
        Node current = first;
        while (i < tasks) {
            ans.append(current.task).append(", ");
            i++;
            current = current.next;
        }
        return ans.substring(0, ans.length() - 2) + "]";
    }

}
