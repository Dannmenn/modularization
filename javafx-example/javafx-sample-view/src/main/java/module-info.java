import pl.mendroch.modularization.example.javafx.api.TabViewProvider;
import pl.mendroch.modularization.example.javafx.sample.SampleViewTabProvider;

module pl.mendroch.example.modularization.javafx.sample {
    requires pl.mendroch.example.modularization.javafx.api;
    requires javafx.controls;
    provides TabViewProvider with SampleViewTabProvider;
}