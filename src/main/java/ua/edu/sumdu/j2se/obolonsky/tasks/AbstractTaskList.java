package ua.edu.sumdu.j2se.obolonsky.tasks;

public abstract class AbstractTaskList {

    public abstract void add(Task task);

    public abstract boolean remove(Task task);

    public abstract int size();

    public abstract Task getTask(int index);

    public abstract ListTypes.types getType();

    /**
     * Returns an {@code LinkedTaskList} or {@code ArrayTaskList} consists of tasks which are executed
     * within specified time period .
     *
     * @param from The start of the given period
     * @param to   The end of the given period
     * @return The {LinkedTaskList}
     */
    public AbstractTaskList incoming(int from, int to) {
        if (from < 0) {
            from = 0;
        }
        AbstractTaskList chosenTasks = TaskListFactory.createTaskList(this.getType());
        Task task;
        if (from >= to) {
            return chosenTasks;
        }
        int tasks = this.size();
        for (int i = 0; i < tasks; i++) {
            task = this.getTask(i);
            if (task.nextTimeAfter(from) > from
                    && task.nextTimeAfter(from) <= to) {
                chosenTasks.add(task);
            }
        }
        return chosenTasks;

    }
}
