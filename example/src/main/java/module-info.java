module pl.mendroch.modularization.example.main {
    exports pl.mendroch.modularization.example;
    uses pl.mendroch.modularization.example.service.ValueProvider;
    requires pl.mendroch.modularization.example.service;
    requires commons.lang3;
    requires pl.mendroch.modularization.core;
    requires pl.mendroch.modularization.common;
    provides pl.mendroch.modularization.common.api.health.HealthEventListener
            with pl.mendroch.modularization.example.LoggingHealthEventListener;
}