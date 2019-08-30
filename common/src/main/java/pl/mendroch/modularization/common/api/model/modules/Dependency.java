package pl.mendroch.modularization.common.api.model.modules;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Dependency {
    private final String group;
    private final String artifact;
    private final String version;

    public Dependency(String text) {
        String[] firstPart = text.split(":");
        String[] secondPart = firstPart[1].split("@");
        this.group = firstPart[0];
        this.artifact = secondPart[0];
        this.version = secondPart[1];
    }

    @Override
    public String toString() {
        return group + ":" + artifact + "@" + version;
    }
}
