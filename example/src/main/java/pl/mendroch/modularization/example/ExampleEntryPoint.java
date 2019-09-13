package pl.mendroch.modularization.example;

import org.apache.commons.lang3.StringUtils;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.example.service.ValueProvider;

import java.util.ServiceLoader;

import static pl.mendroch.modularization.core.runtime.RuntimeManager.RUNTIME_MANAGER;

public class ExampleEntryPoint {
    public static void main(String[] args) throws Exception {
        System.out.println("Before initial print services");
        printServices();
        System.out.println("after initial print services");
        RUNTIME_MANAGER.update(
                new Dependency("pl.mendroch.modularization.example.provider", "1.0-SNAPSHOT"),
                new Dependency("pl.mendroch.modularization.example.provider", "1.1-SNAPSHOT")
        );
        printServices();
        System.exit(0);
    }

    private static void printServices() {
        ServiceLoader<ValueProvider> loader = ServiceLoader.load(ValueProvider.class);
        //noinspection unchecked
        for (ValueProvider<String> valueProvider : loader) {
            String value = valueProvider.provide();
            if (StringUtils.isNotEmpty(value))
                System.out.println(value);
        }
    }
}
