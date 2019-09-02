package pl.mendroch.modularization.example.javafx.view;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import lombok.Synchronized;
import lombok.extern.java.Log;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.core.runtime.RuntimeUpdateListener;
import pl.mendroch.modularization.example.javafx.api.TabViewProvider;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.core.runtime.RuntimeManager.RUNTIME_MANAGER;

@Log
public class ApplicationView extends BorderPane implements RuntimeUpdateListener {
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
        generateTabs();
        RUNTIME_MANAGER.addListener(this);
    }

    private void initializeMenu() {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem closeItem = new MenuItem("Close");
        MenuItem updateItem = new MenuItem("Update Sample view version");
        updateItem.setOnAction(event -> {
            try {
                RUNTIME_MANAGER.update(
                        new Dependency("pl.mendroch.modularization:javafx-sample-view@1.0-SNAPSHOT"),
                        new Dependency("pl.mendroch.modularization:javafx-sample-view@1.1-SNAPSHOT")
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

    @Override
    public void afterUpdate() {
        generateTabs();
    }
}
