package pl.mendroch.modularization.common.api.model.modules;

public class OptionalDependency extends Dependency {
    public OptionalDependency(String group, String artifact, String version) {
        super(group, artifact, version);
    }

    public OptionalDependency(String text) {
        super(text);
    }
}
