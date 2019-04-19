package pl.mendroch.modularization.common.api.model.modules;

import java.util.Objects;

public class Dependency {
    private final String group;
    private final String artifact;
    private final String version;

    public Dependency(String group, String artifact, String version) {
        this.group = group;
        this.artifact = artifact;
        this.version = version;
    }

    public String getGroup() {
        return group;
    }

    public String getArtifact() {
        return artifact;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependency that = (Dependency) o;
        return group.equals(that.group) &&
                artifact.equals(that.artifact) &&
                version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, artifact, version);
    }

    @Override
    public String toString() {
        return group + ":" + artifact + "@" + version;
    }
}
