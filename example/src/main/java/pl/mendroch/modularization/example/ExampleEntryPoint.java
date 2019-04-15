package pl.mendroch.modularization.example;

import pl.mendroch.modularization.example.service.ValueProvider;

import java.util.ServiceLoader;

public class ExampleEntryPoint {
    public static void main(String[] args) {
        ServiceLoader<ValueProvider> loader = ServiceLoader.load(ValueProvider.class);
        for (ValueProvider valueProvider : loader) {
            System.out.println(valueProvider.provide());
        }
    }
}
