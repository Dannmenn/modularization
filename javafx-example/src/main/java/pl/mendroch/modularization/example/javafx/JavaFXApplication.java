package pl.mendroch.modularization.example.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import pl.mendroch.modularization.example.javafx.view.ApplicationView;

import java.nio.file.Paths;

import static pl.mendroch.modularization.core.runtime.ModuleFilesManager.MODULE_FILES_MANAGER;

public class JavaFXApplication extends Application {
    public static void main(String[] args) {
        launch(JavaFXApplication.class, args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(new ApplicationView(), 640, 480);
        stage.setScene(scene);
        stage.show();
        applicationStartedNotification();
        MODULE_FILES_MANAGER.initialize(Paths.get("E:\\temp\\test"));
    }

    private void applicationStartedNotification() {
        Notifications.create()
                .title("Application")
                .text("started successfully")
                .hideAfter(Duration.seconds(2))
                .show();
    }
}
