package pl.mendroch.modularization.example.javafx.api;

import javafx.collections.ObservableList;
import javafx.scene.control.Tab;

public abstract class ReportView extends Tab {
    public ReportView(String text) {
        super(text);
    }

    public abstract void loadData(ObservableList<ReportDataObject> data);
}
