package pl.mendroch.modularization.example.provider;

import pl.mendroch.modularization.example.service.ValueProvider;

import java.lang.module.ModuleDescriptor;

public class VersionValueProvider implements ValueProvider<String> {
    private final String nameAndVersion;

    public VersionValueProvider() {
        ModuleDescriptor descriptor = VersionValueProvider.class.getModule().getDescriptor();
        nameAndVersion = descriptor.toNameAndVersion();
    }

    @Override
    public String provide() {
        return "Value provided by module " + nameAndVersion;
    }
}
