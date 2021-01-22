package ua.edu.sumdu.j2se.obolonsky.app;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import ua.edu.sumdu.j2se.obolonsky.tasks.Task;
import ua.edu.sumdu.j2se.obolonsky.tasks.Tasks;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;

public class ListController {

    @FXML
    private Button back;

    @FXML
    private ScrollPane taskList;

    @FXML
    private Region veil;

    @FXML
    private Pane newTask;

    @FXML
    private TextField title;

    @FXML
    private DatePicker startDate;

    @FXML
    private ComboBox<Integer> startHours;

    @FXML
    private RadioButton nonRepeated;

    @FXML
    private ComboBox<Integer> startMinutes;

    @FXML
    private ToggleButton active;

    @FXML
    private RadioButton repeated;

    @FXML
    private Button editButton;

    @FXML
    private ComboBox<Integer> endHours;

    @FXML
    private ComboBox<Integer> endMinutes;

    @FXML
    private DatePicker endDate;

    @FXML
    private TextField interval;

    @FXML
    private Text endDateText;

    @FXML
    private Text endTimeText;

    @FXML
    private Text end;

    @FXML
    private Text intervalText;

    @FXML
    private Text error;

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> intervalType;

    @FXML
    private Pane date;

    @FXML
    private DatePicker date1;

    @FXML
    private DatePicker date2;

    @FXML
    private Button ok;

    @FXML
    private ComboBox<Integer> hours1;

    @FXML
    private ComboBox<Integer> minutes1;

    @FXML
    private ComboBox<Integer> hours2;

    @FXML
    private ComboBox<Integer> minutes2;

    private final int minSec = 60;
    private final int hourSec = 3600;
    private final int daySec = 86400;
    private final App app = App.getInstance();

    @FXML
    void initialize() {
        setView();
        back.setOnAction(actionEvent -> showMainScene());

        radioGroup();
    }

    private void setView() {
        if (app.isSchedule()) {
            schedulePeriod();
            veil.setVisible(true);
            date.setVisible(true);

            ok.setOnAction(actionEvent -> {

                listConfiguration();
                veil.setVisible(false);
                date.setVisible(false);
            });
        } else {
            listConfiguration();
        }
    }

    private void showMainScene() {
        try {
            Stage stage = (Stage) back.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/app.fxml"));
            loader.load();
            stage.setScene(new Scene(loader.getRoot()));


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private AnchorPane newTaskPane(Task task) {
        Label l = new Label();
        labelTaskCreation(l, task);
        l.setFont(new Font("System", 20));

        MenuButton b = new MenuButton("...");

        MenuItem edit = new MenuItem("edit");
        MenuItem delete = new MenuItem("delete");

        b.getItems().addAll(edit, delete);
        b.setPrefWidth(40);
        b.setPrefHeight(20);


        AnchorPane anchorPane = new AnchorPane(l, b);
        AnchorPane.setRightAnchor(b, 0.0);
        AnchorPane.setLeftAnchor(l, 3.0);
        b.setLayoutY(5);
        anchorPane.setStyle("-fx-border-color: black");
        anchorPane.setPrefHeight(l.getHeight());
        anchorPane.setMaxHeight(Double.POSITIVE_INFINITY);

        edit.setOnAction(actionEvent -> {
            editorSettings(task);
            newTask.setVisible(true);
            veil.setVisible(true);

            editButton.setOnAction(actionEvent1 -> {
                Tasks.deleteTask(app.getMap(), task);
                if (task.isActive()) {
                    app.getTimer().remove(task);
                }
                try {
                    editTask(task);
                    if (app.isSchedule()) {
                        listConfiguration();
                    } else {
                        labelTaskCreation(l, task);
                    }
                    veil.setVisible(false);
                    newTask.setVisible(false);
                    error.setVisible(false);
                } catch (NumberFormatException e) {
                    error.setVisible(true);
                } catch (IllegalArgumentException e) {
                    if (e.getMessage().equals("interval")) {
                        error.setVisible(true);
                        error.setText("The interval must be a positive number");
                    } else if (e.getMessage().equals("start")) {
                        error.setVisible(true);
                        error.setText("The start is already passed");
                    } else {
                        error.setVisible(true);
                        error.setText("The end must be after start");
                    }
                }
            });

            cancelButton.setOnAction(actionEvent1 -> {
                veil.setVisible(false);
                newTask.setVisible(false);
            });
        });

        delete.setOnAction(actionEvent -> {
            if (task.isActive()) {
                app.getTimer().remove(task);
            }
            Tasks.deleteTask(app.getMap(), task);
            if (!app.getMap().isEmpty()){
                app.setFirst();
            } else {
                app.setFirst("Next task");
            }
            app.getList().remove(task);
            if (app.isSchedule()) {
                listConfiguration();
            } else {
                ((VBox) anchorPane.getParent()).getChildren().remove(anchorPane);
            }
        });


        return anchorPane;
    }

    private AnchorPane newTimePane(String s) {
        Label l = new Label(s);
        l.setPrefHeight(60);
        l.setFont(new Font("System", 20));

        AnchorPane p = new AnchorPane(l);
        p.setPrefHeight(60);
        return p;
    }

    private void labelTaskCreation(Label l, Task task) {
        if (app.isSchedule()) {
            l.setText("\t" + task.getTitle());
            l.setPrefHeight(30);
        } else {
            if (task.isRepeated()) {
                String s = task.getTitle() + " : \n" + task.getStartTime() + "  "
                        + task.getEndTime() + "  ";

                if (task.getRepeatInterval() / daySec > 0) {
                    s += task.getRepeatInterval() / daySec + " days";

                } else if (task.getRepeatInterval() / hourSec > 0) {
                    s += task.getRepeatInterval() / hourSec + " hours";

                } else {
                    s += task.getRepeatInterval() / minSec + " minutes";
                }
                l.setText(s);
            } else {
                l.setText(task.getTitle() + " : \n" + task.getStartTime());
            }
            l.setPrefHeight(60);
        }

    }

    private void editTask(Task task) {
        int intervalValue;

        task.setTitle(title.getText());
        LocalDate startDateValue = startDate.getValue();
        LocalTime startTimeValue = LocalTime.of(startHours.getValue(), startMinutes.getValue());
        LocalDateTime startTime = LocalDateTime.of(startDateValue, startTimeValue);
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("start");
        }

        if (repeated.isSelected()) {
            LocalDate endDateValue = endDate.getValue();
            LocalTime endTimeValue = LocalTime.of(endHours.getValue(), endMinutes.getValue());
            intervalValue = interval();

            task.setTime(LocalDateTime.of(startDateValue, startTimeValue),
                    LocalDateTime.of(endDateValue, endTimeValue), intervalValue);
        } else {
            task.setTime(LocalDateTime.of(startDateValue, startTimeValue));
        }
        task.setActive(active.isSelected());
        if (task.isActive()) {
            Tasks.addTask(app.getMap(), task, LocalDateTime.now(), task.getEndTime());
            app.setFirst();
            app.getTimer().add(task);
        }
    }

    private int interval() {
        String type = intervalType.getValue();
        int intervalValue = Integer.parseInt(interval.getText());

        switch (type) {
            case "Minutes":
                intervalValue *= minSec;
                break;
            case "Hours":
                intervalValue *= hourSec;
                break;
            case "Days":
                intervalValue *= daySec;
                break;
        }

        return intervalValue;
    }

    private void editorSettings(Task task) {
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        ObservableList<Integer> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            if (i < 24) {
                hours.add(i);
            }
            minutes.add(i);
        }

        editorSize(task.isRepeated());
        title.setText(task.getTitle());

        startDate.setValue(task.getStartTime().toLocalDate());
        startHours.setItems(hours);
        startHours.setValue(task.getStartTime().getHour());
        startMinutes.setItems(minutes);
        startMinutes.setValue(task.getStartTime().getMinute());

        endDate.setValue(task.getEndTime().toLocalDate());
        endHours.setItems(hours);
        endHours.setValue(task.getEndTime().getHour());
        endMinutes.setItems(minutes);
        endMinutes.setValue(task.getEndTime().getMinute());
        active.setSelected(task.isActive());

        intervalType.setItems(FXCollections.observableArrayList("Minutes", "Hours", "Days"));
        if (task.getRepeatInterval() / daySec > 0) {
            interval.setText(task.getRepeatInterval() / daySec + "");
            intervalType.setValue("Days");
        } else if (task.getRepeatInterval() / hourSec > 0) {
            interval.setText(task.getRepeatInterval() / hourSec + "");
            intervalType.setValue("Hours");
        } else {
            interval.setText(task.getRepeatInterval() / minSec + "");
            intervalType.setValue("Minutes");
        }

        if (task.isRepeated()) {
            repeated.setSelected(true);
        } else {
            nonRepeated.setSelected(true);
        }
    }


    private void editorSize(Boolean b) {
        ObservableList<Node> nodes = FXCollections.observableArrayList(end, endDateText, endTimeText,
                endDate, endHours, endMinutes, interval, intervalText, intervalType);
        if (b) {
            newTask.setPrefHeight(320);
            editButton.setLayoutY(280);
            error.setLayoutY(248);
            for (Node node : nodes) {
                node.setVisible(true);
            }
        } else {
            newTask.setPrefHeight(204);
            editButton.setLayoutY(150);
            error.setLayoutY(158);
            for (Node node : nodes) {
                node.setVisible(false);
            }
        }
    }

    private void schedulePeriod() {
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        ObservableList<Integer> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            if (i < 24) {
                hours.add(i);
            }
            minutes.add(i);
        }

        //scheduleSetting
        hours1.setItems(hours);
        hours1.setValue(0);
        hours2.setItems(hours);
        hours2.setValue(0);
        minutes1.setItems(minutes);
        minutes1.setValue(0);
        minutes2.setItems(minutes);
        minutes2.setValue(0);

        date1.setValue(LocalDate.now());
        date2.setValue(LocalDate.now());

    }

    private void listConfiguration() {
        VBox vBox = new VBox();
        vBox.setSpacing(5);

        if (app.isSchedule()) {
            mapCreation(vBox);
        } else {
            listCreation(vBox);
        }

        taskList.setContent(vBox);
        taskList.fitToWidthProperty().set(true);
    }

    private void listCreation(VBox vBox) {
        for (Task task : app.getList()) {
            vBox.getChildren().add(newTaskPane(task));
        }
    }

    private void mapCreation(VBox vBox) {
        LocalDateTime start = LocalDateTime.of(date1.getValue(), LocalTime.of(hours1.getValue(), minutes1.getValue()));
        LocalDateTime end = LocalDateTime.of(date2.getValue(), LocalTime.of(hours2.getValue(), minutes2.getValue()));
        for (Map.Entry<LocalDateTime, Set<Task>> p : app.getMap().entrySet()) {
            LocalDateTime time = p.getKey();

            if (time.isAfter(start) && time.isBefore(end)){
                vBox.getChildren().add(newTimePane(time.toLocalDate() + "  " + time.getHour()
                        + " : " + time.getMinute()));
                for (Task t : p.getValue()) {
                    vBox.getChildren().add(newTaskPane(t));
                }
            }
        }
    }

    private void radioGroup() {
        ToggleGroup group = new ToggleGroup();
        repeated.setToggleGroup(group);
        nonRepeated.setToggleGroup(group);
        group.selectedToggleProperty().addListener((observableValue, oldT, newT) -> {
            RadioButton rb = (RadioButton) group.getSelectedToggle();

            editorSize(rb.getText().equals("Repeated task"));
        });

    }

}
