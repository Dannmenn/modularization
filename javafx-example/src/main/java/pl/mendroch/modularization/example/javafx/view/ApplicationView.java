package pl.mendroch.modularization.example.javafx.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import lombok.Synchronized;
import pl.mendroch.modularization.example.javafx.api.TabViewProvider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public class ApplicationView extends BorderPane {
    private final Map<String, TabViewProvider> tabProviders = new HashMap<>();
    private final TabPane content = new TabPane();
    private final Menu viewMenu = new Menu("Views");

    private EventHandler<ActionEvent> viewAction = event -> {
        String actionName = ((MenuItem) event.getSource()).getText();
        TabViewProvider provider = tabProviders.get(actionName);
        Tab newTab = provider.provide();
        content.getTabs().add(newTab);
        content.getSelectionModel().select(newTab);
    };

    public ApplicationView() {
        setCenter(content);
        setBottom(new StatusPane());
        initializeMenu();
        initialize();
    }

    private void initializeMenu() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem closeItem = new MenuItem("Close");
        closeItem.setOnAction(event -> {
            Platform.exit();
            System.exit(0);
        });
        fileMenu.getItems().add(closeItem);
        menuBar.getMenus().addAll(fileMenu, viewMenu);
        setTop(menuBar);
    }

    @Synchronized("tabProviders")
    private void initialize() {
        ServiceLoader<TabViewProvider> tabViews = ServiceLoader.load(TabViewProvider.class);
        for (TabViewProvider tabViewProvider : tabViews) {
            tabProviders.put(tabViewProvider.getName(), tabViewProvider);
        }
        viewMenu.getItems().setAll(
                tabProviders.values()
                        .stream()
                        .sorted(Comparator.comparingInt(TabViewProvider::priority))
                        .map(provider -> {
                            MenuItem item = new MenuItem(provider.getName());
                            item.setOnAction(viewAction);
                            return item;
                        })
                        .collect(Collectors.toList())
        );
    }
}
