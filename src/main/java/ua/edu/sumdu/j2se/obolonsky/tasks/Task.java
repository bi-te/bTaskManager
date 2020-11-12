package ua.edu.sumdu.j2se.obolonsky.tasks;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * The {@code Task} class represent several types of tasks
 * and provides methods for manipulating them.
 */
public class Task implements Cloneable {

    private String title;

    private LocalDateTime time;

    private int interval;

    private LocalDateTime start;

    private LocalDateTime end;

    private boolean active;

    public Task(@NotNull String title, @NotNull LocalDateTime time) {

        this.title = title;
        this.time = time;
    }

    public Task(@NotNull String title, @NotNull LocalDateTime start,
                @NotNull LocalDateTime end, int interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("interval is less than or equal to zero");
        } else if (start.isAfter(end)) {
            throw new IllegalArgumentException("start is after end");
        }

        this.title = title;
        this.start = start;
        this.interval = interval;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NotNull String task) {
        this.title = task;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getTime() {
        return start != null ? start : time;
    }

    public void setTime(@NotNull LocalDateTime time) {
        this.time = time;
        this.start = null;
        this.end = null;
        this.interval = 0;
    }

    public LocalDateTime getStartTime() {
        return start != null ? start : time;
    }

    /**
     * Returns the value of {@code end} for repeatable task and
     * {@code time} for non-repeatable task.
     *
     * @return The {@code end} or the {@code time} value
     */
    public LocalDateTime getEndTime() {
        return end != null ? end : time;
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
     * @throws IllegalArgumentException if {@code start} or {@code end}
     *                                  is a negative number, or {@code interval} is a negative number or
     *                                  equals to 0, or {@code start} is greater than {@code end}
     */
    public void setTime(@NotNull LocalDateTime start, @NotNull LocalDateTime end, int interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("interval is less than or equal to zero");
        } else if (start.isAfter(end)) {
            throw new IllegalArgumentException("start is greater than end");
        }
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.time = null;
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
    public LocalDateTime nextTimeAfter(LocalDateTime current) {
        if (current == null) {
            throw new IllegalArgumentException("current is null");
        }
        /* pureIntervals = start + n * interval <= current; (n є N)
         *  pureIntervals <= current < pureIntervals + interval  */
        long pureIntervals;
        LocalDateTime newDate;
        if (active) {
            if (time != null && current.isBefore(time)) {
                return time;
            }
            if (start != null && current.isBefore(start)) {
                return start;
            }
            if (start != null) {
                pureIntervals = (current.toEpochSecond(ZoneOffset.UTC) - ((current.toEpochSecond(ZoneOffset.UTC)
                        - start.toEpochSecond(ZoneOffset.UTC)) % interval));
                newDate = LocalDateTime.ofEpochSecond(pureIntervals + interval,
                        current.getNano(), ZoneOffset.UTC);
                if (!newDate.isAfter(end)) {
                    return newDate;
                }
            }
        }
        return null;
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
        if (time != null) {
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

    @Override
    public Task clone() throws CloneNotSupportedException {
        return (Task) super.clone();
    }
}