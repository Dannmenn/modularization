import pl.mendroch.modularization.example.javafx.api.ReportViewProvider;
import pl.mendroch.modularization.example.javafx.view.TableReportViewProvider;

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

    uses ReportViewProvider;
    provides ReportViewProvider with TableReportViewProvider;
}