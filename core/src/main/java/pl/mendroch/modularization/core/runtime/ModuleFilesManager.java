package pl.mendroch.modularization.core.runtime;

import lombok.extern.java.Log;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.core.JarInfoLoader;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.internal.concurrent.DaemonExceptionAwareThreadFactory.daemonThreadFactory;

@Log
public enum ModuleFilesManager {
    MODULE_FILES_MANAGER;
    private final ExecutorService executor = newCachedThreadPool(daemonThreadFactory("module-file-manager"));
    private final List<ModuleJarInfo> modules = new CopyOnWriteArrayList<>();
    private final List<ModuleChangeListener> listeners = new CopyOnWriteArrayList<>();
    private Path path;

    public void initialize(Path path) throws IOException {
        this.path = path;
        loadModulesInDirectory(path);
        executor.submit(() -> {
            try {
                WatchService watcher = FileSystems.getDefault().newWatchService();
                WatchKey key = path.register(watcher, ENTRY_CREATE, ENTRY_DELETE, OVERFLOW);
                while (key != null) {
                    for (WatchEvent<?> event : key.pollEvents()) {
                        handleWatchEvent(event);
                    }
                    key.reset();
                    key = takeNext(watcher);
                }
            } catch (IOException e) {
                log.log(SEVERE, "Failed to register directory watcher", e);
            }
        });
    }

    public List<ModuleJarInfo> getModules() {
        return List.copyOf(modules);
    }

    private void handleWatchEvent(WatchEvent<?> event) {
        log.info("Event kind:" + event.kind() + ". File affected: " + event.context());
        if (ENTRY_CREATE.equals(event.kind())) {
            onFileCreate(event);
            return;
        }
        if (ENTRY_DELETE.equals(event.kind())) {
            onFileDelete(event);
            return;
        }
        if (OVERFLOW.equals(event.kind())) {
            onOverflow();
        }
    }

    private void onOverflow() {
        try {
            modules.clear();
            loadModulesInDirectory(path);
            notify("modules were reloaded");
        } catch (Exception e) {
            log.log(SEVERE, e.getMessage(), e);
        }
    }

    private void onFileDelete(WatchEvent<?> event) {
        Path context = (Path) event.context();
        Path fileName = context.getFileName();
        for (ModuleJarInfo module : modules) {
            if (module.getJarInfo().getFileName().equals(fileName.toString())) {
                modules.remove(module);
                notify("module " + module.toString() + " was deleted");
            }
        }
    }

    private void onFileCreate(WatchEvent<?> event) {
        Path context = (Path) event.context();
        Path file = Paths.get(path.toString(), context.getFileName().toString());
        if (!Files.isDirectory(file)) {
            ModuleJarInfo loadedJarInfo = JarInfoLoader.loadModuleInformation(file);
            if (isDuplicated(loadedJarInfo.toString())) {
                log.severe("Found duplicate for " + loadedJarInfo.toString());
            } else {
                modules.add(loadedJarInfo);
                notify("New module added: " + loadedJarInfo.getDescriptor().toNameAndVersion());
            }
        } else {
            log.warning("Added file is not a regular file");
        }
    }

    private void loadModulesInDirectory(Path path) throws IOException {
        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .map(JarInfoLoader::loadModuleInformation)
                .forEach(modules::add);
    }

    private boolean isDuplicated(String moduleInfo) {
        for (ModuleJarInfo module : modules) {
            if (moduleInfo.equals(module.toString())) {
                return true;
            }
        }
        return false;
    }

    private WatchKey takeNext(WatchService watcher) {
        try {
            return watcher.take();
        } catch (InterruptedException e) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                //Do nothing
            }
            return takeNext(watcher);
        }
    }

    public void addListener(ModuleChangeListener listener) {
        listeners.add(listener);
    }

    private void notify(String message) {
        executor.submit(() -> {
            for (ModuleChangeListener listener : listeners) {
                listener.onChange(message);
            }
        });
    }
}
