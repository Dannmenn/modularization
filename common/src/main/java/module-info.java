import pl.mendroch.modularization.common.api.health.HealthEventListener;

//TODO CONFIGURATION - export internal to project modules
module pl.mendroch.modularization.common {
    uses HealthEventListener;
    requires java.logging;
    exports pl.mendroch.modularization.common.api;
    exports pl.mendroch.modularization.common.api.model.graph;
    exports pl.mendroch.modularization.common.api.model.modules;
    exports pl.mendroch.modularization.common.api.utils;
    exports pl.mendroch.modularization.common.api.model.tree;
    exports pl.mendroch.modularization.common.api.annotation;
    exports pl.mendroch.modularization.common.internal.concurrent;
    exports pl.mendroch.modularization.common.api.health;
}