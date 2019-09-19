package pl.mendroch.modularization.example.javafx.sample;

import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import pl.mendroch.modularization.example.javafx.api.ReportDataObject;
import pl.mendroch.modularization.example.javafx.api.ReportView;
import pl.mendroch.modularization.example.javafx.api.ReportViewProvider;

public class BarChartViewReportProvider implements ReportViewProvider {
    @Override
    public ReportView provide() {
        return new BarChartReportView("Updated Bar Chart");
    }

    @Override
    public String getName() {
        return "Updated Bar Chart";
    }

    private static class BarChartReportView extends ReportView {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<Number, String> chart = new BarChart<>(yAxis, xAxis);
        private final ObservableList<Data<Number, String>> data;

        BarChartReportView(String text) {
            super(text);
            setContent(chart);
            chart.setTitle(text);
            xAxis.setLabel("Name");
            yAxis.setLabel("Value");
            Series<Number, String> nameSeries = new Series<>();
            nameSeries.setName("Names");
            chart.getData().add(nameSeries);
            data = nameSeries.getData();
        }

        @Override
        public void loadData(ObservableList<ReportDataObject> tableData) {
            tableData.forEach(object -> data.add(new Data<>(object.getValue(), object.getName())));
        }
    }
}
