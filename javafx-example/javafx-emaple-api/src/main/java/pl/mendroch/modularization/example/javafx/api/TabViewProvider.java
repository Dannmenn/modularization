package pl.mendroch.modularization.example.javafx.api;

import javafx.scene.control.Tab;

public interface TabViewProvider<T extends Tab> {
    T provide();

    String getName();

    default int priority() {
        return 100;
    }
}
