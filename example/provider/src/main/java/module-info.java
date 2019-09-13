module pl.mendroch.example.modularization.provider {
    requires pl.mendroch.example.modularization.service;
    exports pl.mendroch.modularization.example.provider;
    provides pl.mendroch.modularization.example.service.ValueProvider with pl.mendroch.modularization.example.provider.StringValueProvider;
}