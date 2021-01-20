package ua.edu.sumdu.j2se.obolonsky.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.Logger;
import ua.edu.sumdu.j2se.obolonsky.tasks.TaskIO;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class StartUp extends Application {
    App app = App.getInstance();
    private final Logger logger = App.getLogger();

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
        } catch (IOException e) {
            logger.error("Error occurred while creating the window",e);
        }

    }
    private void tray(Stage stage) {
        Platform.setImplicitExit(false);

        if (!SystemTray.isSupported()) {
            logger.warn("SystemTray is not supported");
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
            logger.error("Couldn`t add a tray icon", e);
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
            TaskIO.writeText(app.getList(), new File("saves.json"));
        } catch (IOException e) {
            logger.error("Couldn`t save tasks to file", e);
        }
    }

    public static void main(String[] args) {
        launch();
    }
}
