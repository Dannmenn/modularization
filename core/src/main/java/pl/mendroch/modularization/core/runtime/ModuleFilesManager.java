package pl.mendroch.modularization.core.runtime;

import pl.mendroch.modularization.common.api.JarInfoLoader;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.util.logging.Level.SEVERE;
import static pl.mendroch.modularization.common.api.utils.TODO.TODO;

public enum ModuleFilesManager {
    MODULE_FILES_MANAGER;
    private static final Logger LOGGER = Logger.getLogger(ModuleFilesManager.class.getName());

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final List<ModuleJarInfo> modules = new CopyOnWriteArrayList<>();

    public void initialize(Path path) throws IOException {
        modules.clear();
        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .map(JarInfoLoader::loadModuleInformation)
                .forEach(modules::add);
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
                LOGGER.log(SEVERE, "Failed to register directory watcher", e);
            }
        });
    }

    public List<ModuleJarInfo> getModules() {
        return List.copyOf(modules);
    }

    private void handleWatchEvent(WatchEvent<?> event) {
        System.out.println("Event kind:" + event.kind() + ". File affected: " + event.context());
        if (ENTRY_CREATE.equals(event.kind())) {
            TODO("Add entry");
            return;
        }
        if (ENTRY_DELETE.equals(event.kind())) {
            TODO("Remove entry");
            return;
        }
        if (OVERFLOW.equals(event.kind())) {
            TODO("Invalidate whole directory");
        }
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
}
