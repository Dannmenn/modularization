package pl.mendroch.modularization.application.javafx;

import jdk.internal.module.Modules;
import pl.mendroch.modularization.common.api.loader.ModuleConfigurator;

public class JavaFXModuleConfigurator implements ModuleConfigurator {
    @Override
    public void configure(ModuleLayer layer) {
        //noinspection SimplifyOptionalCallChains
        layer.findModule("javafx.graphics").ifPresent(module ->
                layer.findModule("org.controlsfx.controls").ifPresent(to ->
                        Modules.addExports(module, "com.sun.javafx.css", to)));
    }
}
