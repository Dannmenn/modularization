import pl.mendroch.modularization.application.javafx.JavaFXModuleConfigurator;
import pl.mendroch.modularization.common.api.loader.ModuleConfigurator;

module pl.mendroch.modularization.application.javafx.starter {
    requires jdk.unsupported;
    requires pl.mendroch.modularization.application.console;
    requires pl.mendroch.modularization.common;
    provides ModuleConfigurator with JavaFXModuleConfigurator;
}