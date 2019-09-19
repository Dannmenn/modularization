package pl.mendroch.modularization.example.javafx.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.converter.IntegerStringConverter;
import lombok.Synchronized;
import lombok.extern.java.Log;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.core.runtime.RuntimeUpdateListener;
import pl.mendroch.modularization.example.javafx.api.ReportDataObject;
import pl.mendroch.modularization.example.javafx.api.ReportView;
import pl.mendroch.modularization.example.javafx.api.ReportViewProvider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.core.runtime.RuntimeManager.RUNTIME_MANAGER;

@Log
public class ApplicationView extends BorderPane implements RuntimeUpdateListener {
    private final Map<String, ReportViewProvider> tabProviders = new HashMap<>();
    private final TabPane content = new TabPane();
    private final Menu viewMenu = new Menu("Reports");
    private final TableView<ReportDataObject> table = new TableView<>();
    private final ObservableList<ReportDataObject> data = FXCollections.observableArrayList(
            new ReportDataObject("MAZOWIECKIE", 5403412),
            new ReportDataObject("SLASKIE", 4533565),
            new ReportDataObject("WIELKOPOLSKIE", 3493969),
            new ReportDataObject("MALOPOLSKIE", 3400577),
            new ReportDataObject("DOLNOSLASKIE", 2901225),
            new ReportDataObject("LODZKIE", 2466322),
            new ReportDataObject("POMORSKIE", 2333523),
            new ReportDataObject("PODKARPACKIE", 2129015),
            new ReportDataObject("LUBELSKIE", 2117619),
            new ReportDataObject("KUJAWSKO-POMORSKIE", 2077775),
            new ReportDataObject("ZACHODNIOPOMORSKIE", 1701030),
            new ReportDataObject("WARMINSKO-MAZURSKIE", 1428983),
            new ReportDataObject("SWIETOKRZYSKIE", 1241546),
            new ReportDataObject("PODLASKIE", 1181533),
            new ReportDataObject("LUBUSKIE", 1014548),
            new ReportDataObject("OPOLSKIE", 986506)
    );

    private EventHandler<ActionEvent> viewAction = event -> {
        String actionName = ((MenuItem) event.getSource()).getText();
        ReportViewProvider provider = tabProviders.get(actionName);
        ReportView newTab = provider.provide();
        newTab.loadData(FXCollections.unmodifiableObservableList(data));
        content.getTabs().add(newTab);
        content.getSelectionModel().select(newTab);
    };

    public ApplicationView() {
        setCenter(content);
        setBottom(new StatusPane(data));
        initializeMenu();
        generateTabs();
        RUNTIME_MANAGER.addListener(this);
        initializeTable();
    }

    private void initializeTable() {
        TableColumn<ReportDataObject, String> nameColumn = new TableColumn<>("name");
        nameColumn.setEditable(true);
        nameColumn.setCellValueFactory(param -> param.getValue().nameProperty());
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        TableColumn<ReportDataObject, String> descriptionColumn = new TableColumn<>("description");
        descriptionColumn.setEditable(true);
        descriptionColumn.setCellValueFactory(param -> param.getValue().descriptionProperty());
        descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        TableColumn<ReportDataObject, Integer> valueColumn = new TableColumn<>("value");
        valueColumn.setEditable(true);
        valueColumn.setCellValueFactory(param -> param.getValue().valueProperty());
        valueColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        table.getColumns().addAll(nameColumn, descriptionColumn, valueColumn);
        table.setItems(data);
        table.setEditable(true);
        this.content.getTabs().add(new Tab("Table", table));
    }

    private void initializeMenu() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem closeItem = new MenuItem("Close");
        MenuItem updateItem = new MenuItem("Update Sample view version");
        updateItem.setOnAction(event -> {
            try {
                RUNTIME_MANAGER.update(
                        new Dependency("pl.mendroch.example.modularization.javafx.sample", "1.0-SNAPSHOT"),
                        new Dependency("pl.mendroch.example.modularization.javafx.sample", "1.1-SNAPSHOT")
                );
            } catch (Exception e) {
                log.log(SEVERE, "Failed to override dependency version", e);
            }
        });
        closeItem.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
        fileMenu.getItems().addAll(updateItem, closeItem);
        menuBar.getMenus().addAll(fileMenu, viewMenu);
        setTop(menuBar);
    }

    @Synchronized("tabProviders")
    private void generateTabs() {
        tabProviders.clear();
        ServiceLoader<ReportViewProvider> tabViews = ServiceLoader.load(ReportViewProvider.class);
        for (ReportViewProvider reportViewProvider : tabViews) {
            tabProviders.put(reportViewProvider.getName(), reportViewProvider);
        }
        viewMenu.getItems().setAll(
                tabProviders.values()
                        .stream()
                        .sorted(Comparator.comparingInt(ReportViewProvider::priority))
                        .map(provider -> {
                            MenuItem item = new MenuItem(provider.getName());
                            item.setOnAction(viewAction);
                            return item;
                        })
                        .collect(Collectors.toList())
        );
    }

    @Override
    public void afterUpdate() {
        generateTabs();
    }
}
