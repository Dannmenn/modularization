package pl.mendroch.modularization.common.api.model.modules;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.lang.module.ModuleDescriptor;
import java.util.Properties;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ModuleJarInfo {
    @EqualsAndHashCode.Include
    private final JarInfo jarInfo;
    private final Properties dependencyVersions;
    private final ModuleDescriptor descriptor;

    @Override
    public String toString() {
        return descriptor.name() + "@" + jarInfo.getSpecificationVersion();
    }
}
