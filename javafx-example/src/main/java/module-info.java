import pl.mendroch.modularization.example.javafx.api.TabViewProvider;

module pl.mendroch.modularization.example.javafx {
    requires static lombok;
    requires javafx.graphics;
    requires javafx.controls;
    requires org.controlsfx.controls;
    requires pl.mendroch.modularization.example.javafx.api;
    requires pl.mendroch.modularization.core;
    requires java.logging;

    exports pl.mendroch.modularization.example.javafx;

    uses TabViewProvider;
    provides pl.mendroch.modularization.example.javafx.api.TabViewProvider with pl.mendroch.modularization.example.javafx.view.DefaultTabViewProvider;
}