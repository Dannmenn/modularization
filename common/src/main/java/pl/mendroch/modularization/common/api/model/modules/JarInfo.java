package pl.mendroch.modularization.common.api.model.modules;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.nio.file.Path;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class JarInfo {
    @EqualsAndHashCode.Include
    private final String name;
    private final Path path;
    private final String fileName;
    private final String specificationTitle;
    @EqualsAndHashCode.Include
    private final String specificationVersion;
    @EqualsAndHashCode.Include
    private final String implementationVersion;
    private final String mainClass;

    @Override
    public String toString() {
        return name + ":" + specificationVersion;
    }
}
