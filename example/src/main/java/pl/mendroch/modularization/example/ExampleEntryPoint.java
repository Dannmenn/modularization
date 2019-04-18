package pl.mendroch.modularization.example;

import org.apache.commons.lang3.StringUtils;
import pl.mendroch.modularization.example.service.ValueProvider;

import java.util.ServiceLoader;

public class ExampleEntryPoint {
    public static void main(String[] args) {
        ServiceLoader<ValueProvider> loader = ServiceLoader.load(ValueProvider.class);
        //noinspection unchecked
        for (ValueProvider<String> valueProvider : loader) {
            String value = valueProvider.provide();
            if (StringUtils.isNotEmpty(value))
                System.out.println(value);
        }
    }
}
