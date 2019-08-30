package pl.mendroch.modularization.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.lang.module.Configuration;

@Getter
@ToString(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class LoadedModuleReference {
    private final String moduleName;
    @ToString.Include
    private final ModuleJarInfo module;
    private final Configuration configuration;
    private final ModuleLayer layer;
    private final ClassLoader loader;
}
