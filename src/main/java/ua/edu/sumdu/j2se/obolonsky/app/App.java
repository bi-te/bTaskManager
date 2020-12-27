package ua.edu.sumdu.j2se.obolonsky.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import ua.edu.sumdu.j2se.obolonsky.tasks.*;

import java.awt.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class App extends Application {

    private boolean schedule;
    private AbstractTaskList list = new LinkedTaskList();
    private TaskTimer timer;
    private SortedMap<LocalDateTime, Set<Task>> map;
    private StringProperty first = new SimpleStringProperty();
    MainController controller;


    public App() {
        try {
            TaskIO.readText(list, new File("saves.json"));
            timer = new TaskTimer(true) {
                @Override
                public TimerTask newTimerTask(Task task) {
                    TimerTask timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            if (SystemTray.isSupported()){
                                SystemTray tray = SystemTray.getSystemTray();
                                tray.getTrayIcons()[0].displayMessage(task.getTitle(),
                                        "", TrayIcon.MessageType.INFO);

                                map.remove(LocalDateTime.now().withNano(0).withSecond(0));
                                if (!map.isEmpty()){
                                    setFirst();
                                } else {
                                    setFirst("Next task");
                                }
                            }

                            if (task.nextTimeAfter(LocalDateTime.now()) == null){
                                cancel();
                                timer.purge();
                            }
                        }
                    };
                    return timerTask;
                }
            };
            timer.setTimer(list);
            map = Tasks.calendar(list, LocalDateTime.now().withNano(0), timer.getLast());
            if (!map.isEmpty()){
                setFirst();
            } else {
                setFirst("Next task");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/app.fxml"));
            loader.load();

            stage.setTitle("Task Manager");
            stage.setResizable(false);
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/assets/title.png"))));
            stage.setScene(new Scene(loader.getRoot(), 748, 400));
            stage.show();

            tray(stage);

            controller = loader.getController();
            controller.setApp(this);
            controller.newNextTask();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void tray(Stage stage) {
        Platform.setImplicitExit(false);

        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        PopupMenu popup = new PopupMenu();
        SystemTray tray = SystemTray.getSystemTray();

        MenuItem exitItem = new MenuItem("Exit");
        popup.add(exitItem);

        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit()
                .getImage(getClass().getResource("/assets/title.png"));
        TrayIcon trayIcon = new TrayIcon(image);
        trayIcon.setImageAutoSize(true);
        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        trayIcon.addActionListener(e -> Platform.runLater(stage::show));

        exitItem.addActionListener(e -> {
            Platform.exit();
            tray.remove(trayIcon);
        });
    }

    @Override
    public void stop() {
        try {
            TaskIO.writeText(list, new File("saves.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
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

    public void setFirst(){
        LocalDateTime time = map.firstKey();
        StringBuilder s = new StringBuilder(time.toLocalDate().toString() + "  " +
                time.getHour() + " : " + time.getMinute() + "\n\n");
        int c = 0;
        for (Task t: map.get(time)){
            s.append(t.getTitle()).append("  ");
            if (c == 2) {
                s.append(" ...");
            }
            c++;
        }

        setFirst(s.toString());
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