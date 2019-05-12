module pl.mendroch.modularization.application {
    uses pl.mendroch.modularization.application.api.loaders.ApplicationConfigurator;
    uses pl.mendroch.modularization.application.api.loaders.ApplicationModuleLoader;
    requires java.logging;
    requires pl.mendroch.modularization.common;
    requires pl.mendroch.modularization.core;
    exports pl.mendroch.modularization.application.internal to pl.mendroch.modularization.application.console;
    exports pl.mendroch.modularization.application.api;
}