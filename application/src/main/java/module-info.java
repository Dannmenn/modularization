module pl.mendroch.modularization.application {
    requires java.logging;
    requires static lombok;
    requires pl.mendroch.modularization.common;
    requires pl.mendroch.modularization.core;
    exports pl.mendroch.modularization.application.api;
}