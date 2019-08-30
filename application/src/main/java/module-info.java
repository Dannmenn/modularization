module pl.mendroch.modularization.application {
    uses pl.mendroch.modularization.application.api.loaders.ApplicationConfigurator;
    uses pl.mendroch.modularization.application.api.loaders.ApplicationModuleLoader;
    uses pl.mendroch.modularization.application.api.loaders.CustomApplicationLoader;
    requires java.logging;
    requires lombok;
    requires pl.mendroch.modularization.common;
    requires pl.mendroch.modularization.core;
    exports pl.mendroch.modularization.application.internal;
    exports pl.mendroch.modularization.application.api;
}