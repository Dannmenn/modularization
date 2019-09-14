package pl.mendroch.modularization.common.api.model.modules;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Dependency implements Comparable<Dependency> {
    public static final String UNSPECIFIED = "unspecified";
    private final String name;
    private final String version;

    @Override
    public int compareTo(Dependency o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public String toString() {
        return name + "@" + version;
    }
}
