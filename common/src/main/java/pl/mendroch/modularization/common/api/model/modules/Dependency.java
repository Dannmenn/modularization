package pl.mendroch.modularization.common.api.model.modules;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Dependency {
    public static final String UNSPECIFIED = "unspecified";
    private final String name;
    private final String version;

    @Override
    public String toString() {
        return name + "@" + version;
    }
}
