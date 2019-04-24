package pl.mendroch.modularization.example;

import org.apache.commons.lang3.StringUtils;
import pl.mendroch.modularization.example.service.ValueProvider;

import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.nio.file.Paths;
import java.util.List;
import java.util.ServiceLoader;

public class ExampleEntryPoint {
    public static void main(String[] args) {
//        printServices();
        ModuleLayer layer = ExampleEntryPoint.class.getModule().getLayer();
        Configuration configuration = Configuration.resolveAndBind(
                ModuleFinder.of(Paths.get("D:\\repos\\modularization\\example\\provider\\build\\libs\\provider-1.1-SNAPSHOT.jar")),
                List.of(ModuleLayer.boot().configuration()),
                ModuleFinder.of(),
                List.of("pl.mendroch.modularization.example.provider")
        );
        ModuleLayer moduleLayer = ModuleLayer.boot().defineModulesWithOneLoader(configuration, ClassLoader.getSystemClassLoader());
        layer.parents().add(moduleLayer);
        ClassLoader classLoader = moduleLayer.modules().iterator().next().getClassLoader();
        printServices();
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
