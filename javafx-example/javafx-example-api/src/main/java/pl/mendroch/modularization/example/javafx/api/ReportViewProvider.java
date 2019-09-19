package pl.mendroch.modularization.example.javafx.api;

public interface ReportViewProvider<T extends ReportView> {
    T provide();

    String getName();

    /**
     * Priority controls placement of an item on the menu.
     */
    default int priority() {
        return 100;
    }
}
