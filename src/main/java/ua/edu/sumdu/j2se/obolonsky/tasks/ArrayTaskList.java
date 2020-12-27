package ua.edu.sumdu.j2se.obolonsky.tasks;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * The {@code ArrayTaskList} class represents an array of tasks.
 */
public class ArrayTaskList extends AbstractTaskList implements Cloneable {

    /**
     * The array that stores {@code Task} objects.
     */
    private Task[] taskArray;

    /**
     * Constructor that creates a new instance of
     * {@code ArrayTaskList} that has default length = 10.
     */
    public ArrayTaskList() {
        taskArray = new Task[10];
        getClass().getModifiers();
    }

    @NotNull
    @Override
    public TaskArrayIterator iterator() {
        return new TaskArrayIterator();
    }

    private class TaskArrayIterator implements Iterator<Task> {
        private int nextIndex;
        private int currentIndex;
        private boolean checked;

        TaskArrayIterator() {
        }

        @Override
        public boolean hasNext() {
            return nextIndex < tasks;
        }

        @Override
        public Task next() {
            if (!hasNext())
                throw new NoSuchElementException("The iteration has no more elements");

            checked = true;
            currentIndex = nextIndex;
            nextIndex++;
            return taskArray[currentIndex];
        }

        @Override
        public void remove() {
            if (!checked) {
                throw new IllegalStateException();
            }
            taskArray[currentIndex] = null;
            tasks--;
            arrayModification(currentIndex);
            currentIndex--;
            nextIndex--;
            checked = false;
        }
    }


    /**
     * Appends the specified element to the end of the array.
     *
     * @param task The new {@code Task} element
     */
    @Override
    public void add(@NotNull Task task) {
        /*
         * If the array is full, then increases its length twice by
         * creating a new array and copying existing elements to it
         */

        if (tasks == taskArray.length) {
            taskArray = Arrays.copyOf(taskArray, tasks * 2);
        }
        taskArray[tasks] = task;
        tasks++;
    }

    /**
     * Removes the given task from the array.
     *
     * @param task The task to be removed
     * @return {@code true} if element is in the array or
     * {@code false} if not
     */
    @Override
    public boolean remove(Task task) {
        if (task == null) {
            return false;
        }

        int i = tasks - 1;
        boolean exist = false;
        for (; i >= 0; i--) {
            if (taskArray[i].equals(task)) {
                taskArray[i] = null;
                exist = true;
                tasks--;
                break;
            }
        }
        if (exist) {
            arrayModification(i);
        }
        return exist;
    }

    private void arrayModification(int index) {

        for (; index < tasks; index++) {
            taskArray[index] = taskArray[index + 1];
        }

        if (taskArray.length / 4 == tasks && taskArray.length != 10) {
            taskArray = Arrays.copyOf(taskArray, taskArray.length / 2);
        }
    }


    /**
     * Returns the task at the specified position in the array.
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
        return taskArray[index];
    }

    @Override
    public ListTypes.types getType() {
        return ListTypes.types.ARRAY;
    }

    @Override
    public Stream<Task> getStream(){
        return Arrays.stream(taskArray, 0, tasks);
    }

    @Override
    public String toString() {
        if (tasks == 0) {
            return "[]";
        }
        StringBuilder ans = new StringBuilder("[");
        int i = 0;
        while (i < tasks) {
            ans.append(taskArray[i]).append(", ");
            i++;
        }
        return ans.substring(0, ans.length() - 2) + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArrayTaskList tasks1 = (ArrayTaskList) o;
        return tasks == tasks1.tasks &&
                Arrays.equals(taskArray, tasks1.taskArray);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(tasks);
        result = 31 * result + Arrays.hashCode(taskArray);
        return result;
    }

    @Override
    public ArrayTaskList clone() throws CloneNotSupportedException{
        ArrayTaskList copy = (ArrayTaskList) super.clone();
        taskArray = taskArray.clone();
        return copy;
    }
}
