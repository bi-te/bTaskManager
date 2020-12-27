package ua.edu.sumdu.j2se.obolonsky.tasks;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public abstract class TaskTimer extends Timer {
    private final HashMap<Task, TimerTask> taskMap = new HashMap<>();
    private LocalDateTime last = LocalDateTime.now();

    public LocalDateTime getLast() {
        return last;
    }

    public TaskTimer(){}

    public TaskTimer(boolean b) {
        super(b);
    }

    public void setTimer(AbstractTaskList list) {
        for (Task t : list) {
            if (t.isActive()) {
                add(t);
            }
        }
    }

    public void add(Task t) {
        if (t.nextTimeAfter(LocalDateTime.now()) == null) {
            return;
        }
        TimerTask timerTask = newTimerTask(t);



        taskMap.put(t, timerTask);
        if (t.isRepeated()) {
            this.scheduleAtFixedRate(timerTask,
                    Date.from(t.nextTimeAfter(LocalDateTime.now()).atZone(ZoneId.systemDefault()).toInstant()),
                    t.getRepeatInterval() * 1000L);
        } else {
            this.schedule(timerTask,
                    Date.from(t.getStartTime().atZone(ZoneId.systemDefault()).toInstant()));
        }

        last = t.getEndTime().isAfter(last) ? t.getEndTime() : last;
    }


    public abstract TimerTask newTimerTask(Task t);

    public void remove(Task t) {
        TimerTask z = taskMap.get(t);
        if (z != null){
            z.cancel();
            this.purge();
        }
    }

}
