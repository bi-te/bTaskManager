package ua.edu.sumdu.j2se.obolonsky.tasks;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

/**
 * The {@code Task} class represent several types of tasks
 * and provides methods for manipulating them.
 */
public class Task implements Cloneable, Serializable {

    private String title;

    private LocalDateTime time;

    private int interval;

    private LocalDateTime start;

    private LocalDateTime end;

    private boolean active;

    public Task(String title, @NotNull LocalDateTime time) {

        this.title = (title == null) ? "Task 0": title;
        this.time = time;
    }

    public Task(String title, @NotNull LocalDateTime start,
                @NotNull LocalDateTime end, int interval) {
        if (interval <= 0) {
            throw new IllegalArgumentException("interval is less than or equal to zero");
        } else if (start.isAfter(end)) {
            throw new IllegalArgumentException("start is after end");
        }

        this.title = (title == null) ? "Task 0": title;
        this.start = start;
        this.interval = interval;
        this.end = end;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String task) {
        this.title = (task == null) ? "Task 0": task;
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

    public LocalDateTime getEndTime() {
        return end != null ? end : time;
    }

    public int getRepeatInterval() {
        return interval != 0 ? interval : 0;
    }

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

    public boolean isRepeated() {
        return interval != 0;
    }

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