package pl.mendroch.modularization.application.javafx;

import jdk.internal.module.Modules;
import pl.mendroch.modularization.common.api.loader.ThirdPartyModuleConfigurator;

public class JavaFXThirdPartyModuleConfigurator implements ThirdPartyModuleConfigurator {
    @Override
    public void configure(ModuleLayer layer) {
        //noinspection SimplifyOptionalCallChains
        layer.findModule("javafx.graphics").ifPresent(module ->
                layer.findModule("org.controlsfx.controls").ifPresent(to ->
                        Modules.addExports(module, "com.sun.javafx.css", to)));
    }
}
