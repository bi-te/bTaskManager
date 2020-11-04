package ua.edu.sumdu.j2se.obolonsky.tasks;

import java.util.Arrays;
import java.util.stream.Stream;

public abstract class AbstractTaskList implements Iterable<Task> {
    protected int tasks;


    public abstract void add(Task task);

    public abstract boolean remove(Task task);

    public int size() {
        return tasks;
    }

    public abstract Task getTask(int index);

    public abstract ListTypes.types getType();

    public abstract Stream<Task> getStream();

    /**
     * Returns an {@code LinkedTaskList} or {@code ArrayTaskList} consists of tasks which are executed
     * within specified time period .
     *
     * @param from The start of the given period
     * @param to   The end of the given period
     * @return The {LinkedTaskList}
     */
    final public AbstractTaskList incoming(int from, int to) {
        if (from < 0) {
            from = 0;
        }
        AbstractTaskList chosenTasks = TaskListFactory.createTaskList(this.getType());
        if (from >= to) {
            return chosenTasks;
        }

        Stream<Task> stream = chosenTasks.getStream();
        int finalFrom = from;
        stream.filter(t -> t.nextTimeAfter(finalFrom) > finalFrom && t.nextTimeAfter(finalFrom) <= to)
                .forEach(chosenTasks::add);

        return chosenTasks;

    }
}
