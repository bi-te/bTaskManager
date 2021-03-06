package ua.edu.sumdu.j2se.obolonsky.tasks;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Doubly-linked {@code LinkedTaskList} represents a list of tasks.
 */
public class LinkedTaskList extends AbstractTaskList implements Cloneable{
    /**
     * The first node of the list.
     */
    private Node first;

    /**
     * The last node of the list.
     */
    private Node last;

    /**
     * Constructor that creates a new empty list.
     */
    public LinkedTaskList() {
    }

    @NotNull
    @Override
    public TaskListIterator iterator() {
        return new TaskListIterator();
    }

    /**
     * The inner class that represents a node of the list.
     */
    private class Node implements Cloneable{
        Node prev;
        Node next;
        Task task;

        Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }

        @Override
        public Node clone() throws CloneNotSupportedException{
            Node node = (Node) super.clone();
            task = task.clone();
            return node;
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

    @Override
    public void add(@NotNull Task task) {
        Node newTask = new Node(task, last, null);
        Node lastNode = last;
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
    public ListTypes.types getType() {
        return ListTypes.types.LINKED;
    }

    @Override
    public Stream<Task> getStream(){
        return StreamSupport.stream(spliterator(), false);
    }

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

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return  false;
        LinkedTaskList list = (LinkedTaskList) o;
        Iterator<Task> list1 = this.iterator();
        Iterator<Task> list2 = list.iterator();
        while (list1.hasNext() && list2.hasNext()){
            if (!Objects.equals(list1.next(), list2.next())){
                return false;
            }
        }
        return !list1.hasNext() && !list2.hasNext();
    }

    @Override
    public int hashCode(){
        int result = 1;

        for (Object element : this)
            result = 31 * result + (element == null ? 0 : element.hashCode());

        return result;
    }

    @Override
    public LinkedTaskList clone() throws CloneNotSupportedException{
        LinkedTaskList list = (LinkedTaskList) super.clone();
        if (tasks == 0) return list;
        first = first.clone();
        Node node = first;
        while (node.next != null){
            node.next = node.next.clone();
            node.next.prev = node;
            node = node.next;
        }
        return list;
    }
}
