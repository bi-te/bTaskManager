package ua.edu.sumdu.j2se.obolonsky.tasks;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Tasks {
    public static Iterable<Task> incoming(@NotNull Iterable<Task> tasks,
                                          @NotNull LocalDateTime start,
                                          @NotNull LocalDateTime end) {

        LinkedList<Task> chosenTasks = new LinkedList<>();
        if (start.isAfter(end)) {
            return chosenTasks;
        }

        Stream<Task> stream = StreamSupport.stream(tasks.spliterator(), false);
        stream.filter(t -> {
            LocalDateTime next = t.nextTimeAfter(start);
            if (next != null) {
                return !next.isAfter(end);
            }
            return false;
        }).forEach(chosenTasks::addLast);

        return chosenTasks;
    }

    public static SortedMap<LocalDateTime, Set<Task>> calendar(@NotNull Iterable<Task> tasks,
                                                               @NotNull LocalDateTime start,
                                                               @NotNull LocalDateTime end) {
        SortedMap<LocalDateTime, Set<Task>> map = new TreeMap<>();

        LocalDateTime endTime;
        LocalDateTime startTime;
        for (Task task : tasks) {

            if (task.getEndTime().isBefore(end)) {
                endTime = task.getEndTime();
            } else {
                endTime = end;
            }

            startTime = task.nextTimeAfter(start);
            if (startTime == null) {
                continue;
            }

            if (task.isRepeated()) {
                while (!startTime.isAfter(endTime)) {
                    if (map.containsKey(startTime)) {
                        map.get(startTime).add(task);
                    } else {
                        map.put(startTime, new HashSet<>(Collections.singletonList(task)));
                    }
                    startTime = startTime.plusSeconds(task.getRepeatInterval());
                }
            } else {
                if (!start.isAfter(end)) {
                    if (map.containsKey(startTime)) {
                        map.get(startTime).add(task);
                    } else {
                        map.put(startTime, new HashSet<>(Collections.singletonList(task)));
                    }
                }
            }
        }
        return map;
    }
}
