package ua.edu.sumdu.j2se.obolonsky.app;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.edu.sumdu.j2se.obolonsky.tasks.*;

import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class App {
    private final static Logger logger = LogManager.getLogger("App");

    private final File saves = new File("saves.json");

    private boolean schedule;
    private AbstractTaskList list = new LinkedTaskList();
    private TaskTimer timer;
    private SortedMap<LocalDateTime, Set<Task>> map;
    private StringProperty first = new SimpleStringProperty();

    private static App instance;

    private App() {
        try {
            TaskIO.readText(list, saves);
        } catch (IOException e) {
            logger.error("Couldn`t read from file saves.json", e);
        }
        timer = new TaskTimer(true) {
            @Override
            public TimerTask newTimerTask(Task task) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        if (SystemTray.isSupported()) {
                            SystemTray tray = SystemTray.getSystemTray();
                            tray.getTrayIcons()[0].displayMessage(task.getTitle(),
                                    "", TrayIcon.MessageType.INFO);

                            map.remove(LocalDateTime.now().withNano(0).withSecond(0));
                            if (!map.isEmpty()) {
                                setFirst();
                            } else {
                                setFirst("Next task");
                            }
                        }
                        if (task.nextTimeAfter(LocalDateTime.now()) == null) {
                            cancel();
                            timer.purge();
                        }
                        logger.info("Task executed");
                    }
                };
                return timerTask;
            }
        };
        timer.scheduleTimer(list);
        map = Tasks.calendar(list, LocalDateTime.now().withNano(0), timer.getLast());
        if (!map.isEmpty()) {
            setFirst();
        } else {
            setFirst("Next task");
        }

    }

    public static App getInstance() {
        if (instance == null) {
            instance = new App();
        }
        return instance;
    }

    public String getFirst() {
        return first.get();
    }

    public StringProperty firstProperty() {
        return first;
    }

    public void setFirst(String first) {
        this.first.set(first);
    }

    public void setFirst() {
        LocalDateTime time = map.firstKey();
        StringBuilder s = new StringBuilder(time.toLocalDate().toString() + "  " +
                time.getHour() + " : " + time.getMinute() + "\n\n");
        int c = 0;
        for (Task t : map.get(time)) {
            s.append(t.getTitle()).append("  ");
            if (c == 2) {
                s.append(" ...");
            }
            c++;
        }

        setFirst(s.toString());
    }

    public static Logger getLogger() {
        return logger;
    }

    public File getSaves(){
        return saves;
    }

    public boolean isSchedule() {
        return schedule;
    }

    public void setSchedule(boolean schedule) {
        this.schedule = schedule;
    }

    public AbstractTaskList getList() {
        return list;
    }

    public TaskTimer getTimer() {
        return timer;
    }

    public SortedMap<LocalDateTime, Set<Task>> getMap() {
        return map;
    }
}