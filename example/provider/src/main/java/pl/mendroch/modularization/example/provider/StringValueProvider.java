package pl.mendroch.modularization.example.provider;

import org.apache.commons.lang3.StringUtils;
import pl.mendroch.modularization.example.service.ValueProvider;

public class StringValueProvider implements ValueProvider<String> {
    @Override
    public String provide() {
        return StringUtils.center("StringValueProvider", 50, "*");
    }
}
