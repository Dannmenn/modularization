package pl.mendroch.modularization.example.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import pl.mendroch.modularization.example.javafx.view.ApplicationView;

public class JavaFXApplication extends Application {
    public static void main(String[] args) {
        launch(JavaFXApplication.class, args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new ApplicationView(), 640, 480);
        stage.setScene(scene);
        stage.show();
        applicationStartedNotification();
    }

    private void applicationStartedNotification() {
        Notifications.create()
                .title("Application")
                .text("started successfully")
                .hideAfter(Duration.seconds(2))
                .show();
    }
}
