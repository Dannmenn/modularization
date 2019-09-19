import pl.mendroch.modularization.example.javafx.api.ReportViewProvider;
import pl.mendroch.modularization.example.javafx.sample.BarChartViewReportProvider;
import pl.mendroch.modularization.example.javafx.sample.PieChartViewReportProvider;

module pl.mendroch.example.modularization.javafx.sample {
    requires pl.mendroch.example.modularization.javafx.api;
    requires javafx.controls;
    provides ReportViewProvider with BarChartViewReportProvider, PieChartViewReportProvider;
}