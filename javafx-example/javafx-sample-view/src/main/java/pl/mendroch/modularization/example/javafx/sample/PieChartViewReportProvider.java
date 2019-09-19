package pl.mendroch.modularization.example.javafx.sample;

import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import pl.mendroch.modularization.example.javafx.api.ReportDataObject;
import pl.mendroch.modularization.example.javafx.api.ReportView;
import pl.mendroch.modularization.example.javafx.api.ReportViewProvider;

public class PieChartViewReportProvider implements ReportViewProvider {
    @Override
    public ReportView provide() {
        return new PieChartReportView("Updated Pie Chart");
    }

    @Override
    public String getName() {
        return "Updated Pie Chart";
    }

    private static class PieChartReportView extends ReportView {
        final PieChart chart = new PieChart();
        private final ObservableList<Data> data;

        PieChartReportView(String text) {
            super(text);
            setContent(chart);
            chart.setTitle(text);
            data = chart.getData();
        }

        @Override
        public void loadData(ObservableList<ReportDataObject> tableData) {
            tableData.forEach(object -> data.add(new Data(object.getName(), object.getValue())));
        }
    }
}
