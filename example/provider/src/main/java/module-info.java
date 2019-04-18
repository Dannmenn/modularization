module pl.mendroch.modularization.example.provider {
    requires pl.mendroch.modularization.example.service;
    requires commons.lang3;
    exports pl.mendroch.modularization.example.provider;
    provides pl.mendroch.modularization.example.service.ValueProvider with pl.mendroch.modularization.example.provider.StringValueProvider;
}