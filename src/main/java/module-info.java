module ua.edu.sumdu.j2se.obolonsky {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires com.google.gson;
    requires java.desktop;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens ua.edu.sumdu.j2se.obolonsky.app to javafx.fxml;
    opens ua.edu.sumdu.j2se.obolonsky.tasks to com.google.gson;

    exports ua.edu.sumdu.j2se.obolonsky.tasks;
    exports ua.edu.sumdu.j2se.obolonsky.app;

}