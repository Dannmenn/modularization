import pl.mendroch.modularization.example.javafx.api.TabViewProvider;
import pl.mendroch.modularization.example.javafx.view.DefaultTabViewProvider;

module pl.mendroch.example.modularization.javafx {
    requires static lombok;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.controlsfx.controls;
    requires pl.mendroch.example.modularization.javafx.api;
    requires pl.mendroch.modularization.core;
    requires java.logging;
    requires pl.mendroch.modularization.common;

    exports pl.mendroch.modularization.example.javafx;

    uses TabViewProvider;
    provides TabViewProvider with DefaultTabViewProvider;
}