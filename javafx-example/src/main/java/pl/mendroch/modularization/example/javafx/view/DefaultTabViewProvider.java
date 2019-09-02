package pl.mendroch.modularization.example.javafx.view;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import pl.mendroch.modularization.example.javafx.api.TabViewProvider;

public class DefaultTabViewProvider implements TabViewProvider<Tab> {
    @Override
    public Tab provide() {
        return new Tab("Default Tab View", new Label("Default View content"));
    }

    @Override
    public String getName() {
        return "Default";
    }
}
