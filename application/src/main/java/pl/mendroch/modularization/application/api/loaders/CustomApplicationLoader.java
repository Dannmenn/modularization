package pl.mendroch.modularization.application.api.loaders;

import java.util.List;

@SuppressWarnings("unused")
public interface CustomApplicationLoader {
    default boolean runConfigurators(List<ApplicationConfigurator> configurators) {
        return true;
    }

    default boolean runModuleLoaders(List<ApplicationModuleLoader> moduleLoaders) {
        return true;
    }

    void beforeLoad();

    void afterLoad();
}
