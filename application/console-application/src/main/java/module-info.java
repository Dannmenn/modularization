module pl.mendroch.modularization.application.console {
    exports pl.mendroch.modularization.application.console.api;
    requires java.logging;
    requires static lombok;
    requires pl.mendroch.modularization.application;
}