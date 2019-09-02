import pl.mendroch.modularization.common.api.health.HealthEventListener;

module pl.mendroch.modularization.common {
    uses HealthEventListener;
    requires static lombok;
    requires static org.mapstruct.processor;
    requires java.logging;
    exports pl.mendroch.modularization.common.api.model.graph;
    exports pl.mendroch.modularization.common.api.model.modules;
    exports pl.mendroch.modularization.common.api.utils;
    exports pl.mendroch.modularization.common.api.model.tree;
    exports pl.mendroch.modularization.common.api.annotation;
    exports pl.mendroch.modularization.common.internal.concurrent;
    exports pl.mendroch.modularization.common.api.health;
    exports pl.mendroch.modularization.common.api.loader;
}