module pl.mendroch.modularization.application.javafx.starter {
    requires jdk.unsupported;
    requires pl.mendroch.modularization.application.console;
    requires pl.mendroch.modularization.common;
    provides pl.mendroch.modularization.common.api.loader.ThirdPartyModuleConfigurator with pl.mendroch.modularization.application.javafx.JavaFXThirdPartyModuleConfigurator;
}