module pl.mendroch.modularization.core {
    uses pl.mendroch.modularization.common.api.loader.ThirdPartyModuleConfigurator;
    exports pl.mendroch.modularization.core;
    exports pl.mendroch.modularization.core.runtime;
    requires pl.mendroch.modularization.common;
    requires java.logging;
    requires static lombok;
}