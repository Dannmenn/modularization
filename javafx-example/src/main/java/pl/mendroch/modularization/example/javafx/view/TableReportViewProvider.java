package pl.mendroch.modularization.example.javafx.view;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import pl.mendroch.modularization.example.javafx.api.ReportDataObject;
import pl.mendroch.modularization.example.javafx.api.ReportView;
import pl.mendroch.modularization.example.javafx.api.ReportViewProvider;

public class TableReportViewProvider implements ReportViewProvider<ReportView> {
    @Override
    public ReportView provide() {
        return new TableReportView("Table Report");
    }

    @Override
    public String getName() {
        return "Table";
    }

    private static class TableReportView extends ReportView {
        private final TableView<ReportDataObject> table = new TableView<>();

        TableReportView(String text) {
            super(text);
            setContent(table);
            TableColumn<ReportDataObject, String> nameColumn = new TableColumn<>("name");
            nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
            TableColumn<ReportDataObject, String> descriptionColumn = new TableColumn<>("description");
            descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
            TableColumn<ReportDataObject, Integer> valueColumn = new TableColumn<>("value");
            valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
            table.getColumns().addAll(nameColumn, descriptionColumn, valueColumn);
        }

        @Override
        public void loadData(ObservableList<ReportDataObject> data) {
            table.setItems(data);
        }
    }
}
