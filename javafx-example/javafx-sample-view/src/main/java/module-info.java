import pl.mendroch.modularization.example.javafx.api.TabViewProvider;
import pl.mendroch.modularization.example.javafx.sample.SampleViewTabProvider;

module pl.mendroch.modularization.example.javafx.sample {
    requires pl.mendroch.modularization.example.javafx.api;
    requires javafx.controls;
    provides TabViewProvider with SampleViewTabProvider;
}