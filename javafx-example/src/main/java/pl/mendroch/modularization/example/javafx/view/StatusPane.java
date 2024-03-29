package pl.mendroch.modularization.example.javafx.view;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.util.Duration;
import lombok.extern.java.Log;
import org.controlsfx.control.Notifications;
import org.controlsfx.control.StatusBar;
import org.controlsfx.glyphfont.Glyph;
import pl.mendroch.modularization.core.runtime.ModuleChangeListener;
import pl.mendroch.modularization.example.javafx.api.ReportDataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.logging.Level.SEVERE;
import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;
import static pl.mendroch.modularization.core.runtime.ModuleFilesManager.MODULE_FILES_MANAGER;
import static pl.mendroch.modularization.core.runtime.RuntimeManager.RUNTIME_MANAGER;

@Log
public class StatusPane extends StatusBar implements ModuleChangeListener {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Button infoButton = new Button("", new Glyph("FontAwesome", "INFO_CIRCLE"));
    private final Dialog<Boolean> dialog = new Dialog<>();
    private final List<String> infoMessages = new ArrayList<>();

    StatusPane(ObservableList<ReportDataObject> data) {
        MODULE_FILES_MANAGER.addListener(this);
        ButtonType cancelButtonType = new ButtonType("Cancel", CANCEL_CLOSE);
        ButtonType loginButtonType = new ButtonType("Update", OK_DONE);
        dialog.setTitle("Modules were updated");
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, cancelButtonType);
        dialog.setResizable(true);
        dialog.setResultConverter(param -> {
            if (param.getButtonData() == OK_DONE) {
                try {
                    executor.submit(() -> {
                        try {
                            RUNTIME_MANAGER.update();
                        } catch (Exception e) {
                            throw new IllegalStateException(e);
                        }
                    }).get();
                } catch (Exception e) {
                    log.log(SEVERE, e.getMessage(), e);
                    return false;
                }
                infoMessages.clear();
                getRightItems().remove(infoButton);
                return true;
            }
            return false;
        });
        infoButton.setOnAction(event -> {
            dialog.setContentText(String.join("\n", infoMessages));
            dialog.showAndWait();
        });
        Button addTableItemButton = new Button("", new Glyph("FontAwesome", "PLUS_CIRCLE"));
        getLeftItems().add(addTableItemButton);
        addTableItemButton.setOnAction(event -> data.add(new ReportDataObject()));
    }

    @Override
    public void onChange(String message) {
        Platform.runLater(() -> {
            Notifications.create()
                    .owner(this)
                    .title(message)
                    .text("Refresh view to apply changes")
                    .hideAfter(Duration.seconds(3))
                    .threshold(1,
                            Notifications.create()
                                    .owner(this)
                                    .title("Modules were updated")
                                    .text("Refresh view to apply changes")
                                    .hideAfter(Duration.seconds(3)))
                    .showInformation();
            infoMessages.add(message);
            getRightItems().setAll(infoButton);
        });
    }
}
