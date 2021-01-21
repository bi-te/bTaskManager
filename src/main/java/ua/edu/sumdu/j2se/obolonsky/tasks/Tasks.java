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
        TreeMap<LocalDateTime, Set<Task>> map = new TreeMap<>();

        for (Task task : tasks) {
            addTask(map, task, start, end);
        }

        return map;
    }

    public static void addTask(@NotNull SortedMap<LocalDateTime, Set<Task>> map, @NotNull Task task,
                               @NotNull LocalDateTime start,
                               @NotNull LocalDateTime end){
        LocalDateTime endTime;
        LocalDateTime startTime;

        if (task.getEndTime().isBefore(end)) {
            endTime = task.getEndTime();
        } else {
            endTime = end;
        }

        startTime = task.nextTimeAfter(start);
        if (startTime == null) {
            return;
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
            if (!start.isAfter(endTime)) {
                if (map.containsKey(startTime)) {
                    map.get(startTime).add(task);
                } else {
                    map.put(startTime, new HashSet<>(Collections.singletonList(task)));
                }
            }
        }
    }

    public static void deleteTask(@NotNull SortedMap<LocalDateTime, Set<Task>> map, @NotNull Task task){
        LocalDateTime startTime = task.getStartTime();
        do {
            if(map.containsKey(startTime)){
                Set<Task> s = map.get(startTime);
                s.remove(task);
                if(s.size() == 0){
                    map.remove(startTime);
                }
            }
            if (!task.isRepeated()){
                break;
            }
            startTime = startTime.plusSeconds(task.getRepeatInterval());
        } while (!startTime.isAfter(task.getEndTime()));
    }
}
