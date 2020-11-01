package ua.edu.sumdu.j2se.obolonsky.tasks;


import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * The {@code ArrayTaskList} class represents an array of tasks.
 */
public class ArrayTaskList extends AbstractTaskList {

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
    }


    /**
     * Appends the specified element to the end of the array.
     *
     * @param task The new {@code Task} element
     */
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

        /* if removed task was not the last, replaces the last task to the position of the removed one
         * to make continuous sequence */
        if (exist && i != tasks) {
            taskArray[i] = taskArray[tasks];
            taskArray[tasks] = null;
        }

        /* creates a new array with existing elements but with the length reduced twice
         * to not take up extra space*/
        if (exist && taskArray.length / 4 == tasks && taskArray.length != 10) {
            taskArray = Arrays.copyOf(taskArray, taskArray.length / 2);
        }
        return exist;
    }


    /**
     * Returns the task at the specified position in the array.
     *
     * @param index The position from where task will be taken
     * @return The {@code Task} object
     * @throws IndexOutOfBoundsException if {@code index} is out of range.
     */
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


    /**
     * Returns a string representation of the contents of the array.
     * The string representation consists of a list of the array's elements,
     * enclosed in square brackets ({@code "[]"}).  Adjacent elements are
     * separated by the characters {@code ", "} (a comma followed by a
     * space).
     *
     * @return A string representation of the array
     */
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
}
