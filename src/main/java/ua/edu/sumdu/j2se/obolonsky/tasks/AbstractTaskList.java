package ua.edu.sumdu.j2se.obolonsky.tasks;

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

}
