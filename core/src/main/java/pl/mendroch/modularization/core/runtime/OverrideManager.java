package pl.mendroch.modularization.core.runtime;

import pl.mendroch.modularization.common.api.DependencyGraphUtils;
import pl.mendroch.modularization.common.api.annotation.PerformanceOptimizationHint;
import pl.mendroch.modularization.common.api.model.modules.Dependency;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.api.health.HealthRegister.HEALTH_REGISTER;
import static pl.mendroch.modularization.core.runtime.ModuleFilesManager.MODULE_FILES_MANAGER;

public enum OverrideManager {
    OVERRIDE_MANAGER;
    private static final Logger LOGGER = Logger.getLogger(OverrideManager.class.getName());

    private final Map<Dependency, Dependency> overrides = new HashMap<>();
    private final Path propertiesFile = Paths.get("module-overrides.properties");
    @PerformanceOptimizationHint
    private int cleanupDelay = 10;

    OverrideManager() {
        try (final InputStream inputStream = Files.newInputStream(propertiesFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                overrides.put(new Dependency(entry.getKey().toString()), new Dependency(entry.getValue().toString()));
            }
        } catch (Exception e) {
            HEALTH_REGISTER.registerEvent(SEVERE, e);
        }
    }

    public synchronized void override(Dependency existing, Dependency override) {
        for (Map.Entry<Dependency, Dependency> entry : overrides.entrySet()) {
            if (entry.getValue().equals(existing)) {
                overrides.put(entry.getKey(), override);
            }
        }
        overrides.put(existing, override);

        cleanupOverrides();
        saveOverridesToFile();
    }

    private void saveOverridesToFile() {
        Properties properties = new Properties();
        for (Map.Entry<Dependency, Dependency> entry : overrides.entrySet()) {
            properties.put(entry.getKey().toString(), entry.getValue().toString());
        }
        try (OutputStream outputStream = Files.newOutputStream(propertiesFile)) {
            properties.store(outputStream, "Modules override values");
        } catch (IOException e) {
            LOGGER.log(SEVERE, e.getMessage(), e);
            HEALTH_REGISTER.registerEvent(SEVERE, e);
        }
    }

    private void cleanupOverrides() {
        if (--cleanupDelay > 0) return;
        cleanupDelay = 10;
        List<Dependency> modules = MODULE_FILES_MANAGER
                .getModules()
                .stream()
                .map(DependencyGraphUtils::convertModuleToDependency)
                .collect(Collectors.toUnmodifiableList());
        for (Dependency dependency : overrides.keySet()) {
            if (!modules.contains(dependency)) overrides.remove(dependency);
        }
    }

    public synchronized Map<Dependency, Dependency> getOverrides() {
        return Map.copyOf(overrides);
    }
}
