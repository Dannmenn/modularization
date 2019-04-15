package pl.mendroch.modularization.example.provider;

import pl.mendroch.modularization.example.service.ValueProvider;

public class StringValueProvider implements ValueProvider<String> {
    @Override
    public String provide() {
        return "StringValueProvider:TEXT";
    }
}
