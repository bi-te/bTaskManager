package ua.edu.sumdu.j2se.obolonsky.tasks;

public class TaskListFactory {
    public static AbstractTaskList createTaskList(ListTypes.types type) {
        switch (type) {
            case LINKED:
                return new LinkedTaskList();
            case ARRAY:
                return new ArrayTaskList();
            default:
                throw new IllegalArgumentException("The parameter 'type' - " + type + " is wrong");
        }
    }
}
