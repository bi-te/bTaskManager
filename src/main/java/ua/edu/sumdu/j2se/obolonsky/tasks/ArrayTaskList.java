package ua.edu.sumdu.j2se.obolonsky.tasks;


import java.util.Arrays;

/**
 * The {@code ArrayTaskList} class represents an array of tasks.
 */
public class ArrayTaskList {
    /**
     * The number of tasks in the array.
     */
    private int tasks;

    /**
     * The array that stores {@code Task} objects.
     */
    private Task[] taskArray;

    /**
     * Constructor that creates a new instance of
     * {@code ArrayTaskList} that has default length = 0.
     */
    public ArrayTaskList() {
        taskArray = new Task[10];
    }


    /**
     * Appends the specified element to the end of the array.
     *
     * @param task The new {@code Task} element
     */
    public void add(Task task) {
        /*
         * If the array is full, then increases its length by 10 by
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
        int i = tasks - 1;
        boolean exist = false;
        for (; i >= 0; i--) {
            if (task.equals(taskArray[i])) {
                taskArray[i] = null;
                exist = true;
                tasks--;
                break;
            }
        }

        /* if removed task was not the last, replaces the last task to the position of the removed one
         * to make continuous sequence */
        if (i != tasks) {
            taskArray[i] = taskArray[tasks];
            taskArray[tasks] = null;
        }

        /* creates a new array with existing elements but with the length reduced by 10
         * to not take up extra space*/
        if (exist && taskArray.length / 4 == tasks && taskArray.length != 10) {
            taskArray = Arrays.copyOf(taskArray, taskArray.length / 2);
        }
        return exist;
    }


    /**
     * Returns the number of tasks in the array.
     *
     * @return The {@code int} value - number of tasks
     */
    public int size() {
        return tasks;
    }


    /**
     * Returns the task at the specified position in the array.
     *
     * @param index The position from where task will be taken
     * @return The {@code Task} object
     */
    public Task getTask(int index) {
        return taskArray[index];
    }

    /**
     * Returns the array of tasks.
     *
     * @return The {@code Task[]} array
     */
    private Task[] getArray() {
        return taskArray;
    }

    /**
     * Sets the array of tasks to the new specified
     * {@code Task[]} array.
     *
     * @param tasks The specified {@code Task[]} array
     */
    private void setArray(Task[] tasks) {
        taskArray = tasks;
    }

    /**
     * Returns an {@code ArrayTaskList} consists of tasks which are executed
     * within specified time period .
     *
     * @param from The start of the given period
     * @param to   The end of the given period
     * @return The {ArrayTaskList}
     */
    public ArrayTaskList incoming(int from, int to) {

        ArrayTaskList chosenTasks = new ArrayTaskList();
        int count = 0;
        for (int i = 0; i < tasks; i++) {
            if (taskArray[i].nextTimeAfter(from) > from
                    && taskArray[i].nextTimeAfter(from) <= to) {
                chosenTasks.add(taskArray[i]);
                count++;
            }
        }
        chosenTasks.setArray(Arrays.copyOf(chosenTasks.getArray(), count));
        return chosenTasks;
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
