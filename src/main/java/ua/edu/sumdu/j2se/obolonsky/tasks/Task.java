package ua.edu.sumdu.j2se.obolonsky.tasks;

import java.util.Objects;

/**
 * The {@code Task} class represent several types of tasks
 * and provides methods for manipulating them.
 */
public class Task {

    /**
     * The name of the task.
     */
    private String title;

    /**
     * The time when non-repeatable task will be executed.
     */
    private int time;

    /**
     * The interval at which the repeatable task is executed.
     */
    private int interval;

    /**
     * Time when the repeatable task will be executed.
     */
    private int start;

    /**
     * Time until which the repeatable task can be executed.
     */
    private int end;

    /**
     * The status of activity of the task.
     */
    private boolean active;

    /**
     * Initializes a newly created {@code Task} object that
     * is non-repeatable and executes only once.
     *
     * @param title The name of the task
     * @param time  Time when the task will be executed
     */
    public Task(String title, int time) {
        this.title = title;
        this.time = time;
    }

    /**
     * Initializes a newly created {@code Task} object that
     * is repeatable and executes several times within time
     * period from {@code start} to {@code end} with fixed
     * interval.
     *
     * @param title    The name of the task
     * @param start    Time when the task will be executed
     * @param end      Time until which the task can be executed
     * @param interval The interval at which the task is executed
     */
    public Task(String title, int start, int end, int interval) {
        this.title = title;
        this.start = start;
        this.interval = interval;
        this.end = end;
    }

    /**
     * Returns the title of the task.
     *
     * @return the {@code title} of the task
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of object to the new specified {@code String} value.
     *
     * @param task The new value of the {@code title} of the task
     */
    public void setTitle(String task) {
        this.title = task;
    }

    /**
     * Returns the status of activity of the task.
     *
     * @return {@code true} if the task is active or {@code false} if no
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the value of {@code active} to the new specified value.
     *
     * @param active The new value of the {@code active}
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Returns the value of {@code time} for non-repeatable task
     * and the value of {@code start} for repeatable task.
     *
     * @return the {@code time} or the {@code start} value
     */
    public int getTime() {
        return start != 0 ? start : time;
    }

    /**
     * Sets the {@code time} of the non-repeatable task to the new specified
     * {@code int} value. If the task is repeatable, makes it non-repeatable
     * and sets the {@code time} value
     *
     * @param time The new value of {@code time}
     */
    public void setTime(int time) {
        this.time = time;
        this.start = 0;
        this.end = 0;
        this.interval = 0;
    }

    /**
     * Returns the value of {@code start} for repeatable task
     * and the value of {@code time} for non-repeatable task.
     *
     * @return The {@code start} or the {@code time} value
     */
    public int getStartTime() {
        return start != 0 ? start : time;
    }

    /**
     * Returns the value of {@code end} for repeatable task and
     * {@code time} for non-repeatable task.
     *
     * @return The {@code end} or the {@code time} value
     */
    public int getEndTime() {
        return end != 0 ? end : time;
    }

    /**
     * Returns the value of {@code interval} for repeatable task
     * and {@code 0} for non-repeatable task.
     *
     * @return {@code interval} or {@code 0}
     */
    public int getRepeatInterval() {
        return interval != 0 ? interval : 0;
    }

    /**
     * Sets the time configuration ({@code start},{@code end} and
     * {@code interval} values) of repeatable task to the new
     * specified {@code int} values.If the task is non-repeatable,
     * makes it repeatable with specified time configuration values.
     *
     * @param start    The new value of {@code start}
     * @param end      The new value of {@code end}
     * @param interval The new value of {@code interval}
     */
    public void setTime(int start, int end, int interval) {
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.time = 0;
    }

    /**
     * Checks whether the task is repeatable and return
     * {@code boolean} value.
     *
     * @return {@code true} if the task is repeated or
     * {@code false} if it is not repeated
     */
    public boolean isRepeated() {
        return interval != 0;
    }

    /**
     * Returns the time when the task will be executed next time.
     * For non-repeatable tasks it returns the value of {@code time}
     * and for repeatable - time when the task is executed next.
     * Returns {@code -1} if tasks won`t be executed anymore.
     *
     * @param current The time when the method is called
     * @return the time when the task will be executed next time or
     * {@code -1} if that time does not exist
     */
    public int nextTimeAfter(int current) {
        /* pureIntervals = start + n * interval <= current; (n є N)
         *  pureIntervals <= current < pureIntervals + interval  */
        int pureIntervals;
        if (active) {
            if (time != 0 && current < time) {
                return time;
            }
            if (start > current) {
                return start;
            }
            if (start != 0) {
                pureIntervals = (current - ((current - start) % interval));
                if (pureIntervals < end - interval) {
                    return pureIntervals + interval;
                }
            }
        }
        return -1;
    }


    /**
     * Returns a string representation of the task.
     * The string representation consists of the title of task and
     * its time configuration ( {@code start},{@code end} and
     * {@code interval} for repeatable tasks or {@code time} for
     * non-repeatable). Title and time configuration are separated by
     * {@code ": "}(a colon followed by a space). For repeatable tasks
     * time configuration is enclosed in braces ( {@code "{}"}) and
     * has such construction : {@code "start - value, end - value, interval - value"}
     *
     * @return a string representation of {@code Task}
     */
    @Override
    public String toString() {
        String task;
        if (time != 0) {
            task = this.title + ": " + time;
        } else {
            task = this.title + ": { start - " + start
                    + ", end - " + end + ", interval - " + interval
                    + "}";
        }
        return task;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return time == task.time &&
                interval == task.interval &&
                start == task.start &&
                end == task.end &&
                title.equals(task.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, time, interval, start, end);
    }
}