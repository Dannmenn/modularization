package pl.mendroch.modularization.example.javafx.sample;

import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import pl.mendroch.modularization.example.javafx.api.TabViewProvider;

public class SampleViewTabProvider implements TabViewProvider {
    @Override
    public Tab provide() {
        return new Tab("Sample", new Label("Sample View content"));
    }

    @Override
    public String getName() {
        return "Sample";
    }
}
