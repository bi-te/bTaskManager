package my.test.fx;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tasks.Task;
import tasks.Tasks;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class MainController {

    @FXML
    private Label nextTask;

    @FXML
    private Button addButton;

    @FXML
    private Button listView;

    @FXML
    private Button schedule;

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
    private Button create;

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

    private App app;


    @FXML
    void initialize() {
        radioGroup();

        addButton.setOnAction(actionEvent -> {
            veil.setVisible(true);
            newTask.setVisible(true);
            editorView();

            create.setOnAction(actionEvent1 -> {
                try {
                    addTask();
                    newTask.setVisible(false);
                    veil.setVisible(false);
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
                newTask.setVisible(false);
                veil.setVisible(false);
                error.setVisible(false);
            });
        });

        listView.setOnAction(actionEvent -> {
            app.setSchedule(false);
            showList();
        });

        schedule.setOnAction(actionEvent -> {
            app.setSchedule(true);
            showList();
        });

    }

    public void setApp(App app) {
        this.app = app;
    }

    public void newNextTask(){
        app.firstProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                Platform.runLater(() -> nextTask.setText(app.getFirst()));
            }
        });
        nextTask.setText(app.getFirst());
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

    private void showList() {
        try {
            Stage stage = (Stage) listView.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/list.fxml"));
            loader.load();

            stage.setScene(new Scene(loader.getRoot()));
            ListController controller = loader.getController();
            controller.setApp(app);
            controller.setView();
        } catch (IOException e){
            e.printStackTrace();
        }

        app.firstProperty().removeListener((observableValue, s, t1) -> nextTask.setText(t1));
    }

    private void editorView() {
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        ObservableList<Integer> minutes = FXCollections.observableArrayList();
        for (int i = 0; i < 60; i++) {
            if (i < 24) {
                hours.add(i);
            }
            minutes.add(i);
        }

        startDate.setValue(LocalDate.now());
        startHours.setItems(hours);
        startHours.setValue(0);
        startMinutes.setItems(minutes);
        startMinutes.setValue(0);

        endDate.setValue(LocalDate.now());
        endHours.setItems(hours);
        endHours.setValue(0);
        endMinutes.setItems(minutes);
        endMinutes.setValue(0);

        intervalType.setItems(FXCollections.observableArrayList("Minutes", "Hours", "Days"));
        intervalType.setValue("Minutes");
        nonRepeated.setSelected(true);

        title.setText("");
        active.setSelected(false);
    }

    private void addTask() {
        Task task;
        int intervalValue;

        LocalDate startDateValue = startDate.getValue();
        LocalTime startTimeValue = LocalTime.of(startHours.getValue(), startMinutes.getValue());
        LocalDateTime startTime = LocalDateTime.of(startDateValue, startTimeValue);
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("start");
        }

        String taskTitle = title.getText();
        if (repeated.isSelected()) {
            LocalDate endDateValue = endDate.getValue();
            LocalTime endTimeValue = LocalTime.of(endHours.getValue(), endMinutes.getValue());
            intervalValue = interval();
            task = new Task(taskTitle, startTime,
                    LocalDateTime.of(endDateValue, endTimeValue), intervalValue);
        } else {
            task = new Task(taskTitle, LocalDateTime.of(startDateValue, startTimeValue));
        }
        task.setActive(active.isSelected());

        app.getList().add(task);
        if (task.isActive()) {
            app.getTimer().add(task);
        }
        Tasks.addTask(app.getMap(), task, LocalDateTime.now().withNano(0), task.getEndTime());
        app.setFirst();
    }

    private int interval() {
        String type = intervalType.getValue();
        int intervalValue = Integer.parseInt(interval.getText());

        switch (type) {
            case "Minutes":
                intervalValue *= 60;
                break;
            case "Hours":
                intervalValue *= 3600;
                break;
            case "Days":
                intervalValue *= 86400;
                break;
        }

        return intervalValue;
    }

    private void editorSize(Boolean b) {
        ObservableList<Node> nodes = FXCollections.observableArrayList(end, endDateText, endTimeText,
                endDate, endHours, endMinutes, interval, intervalText, intervalType);
        if (b) {
            newTask.setPrefHeight(320);
            create.setLayoutY(280);
            error.setLayoutY(248);
            for (Node node : nodes) {
                node.setVisible(true);
            }
        } else {
            newTask.setPrefHeight(204);
            create.setLayoutY(150);
            error.setLayoutY(158);
            for (Node node : nodes) {
                node.setVisible(false);
            }
        }
    }
}