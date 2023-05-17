package antivoland.jh.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileStorage {
    private static final Path DEFAULT_ROOT = Paths.get(System.getProperty("user.dir"));

    protected final Path root;

    FileStorage() {
        this(DEFAULT_ROOT);
    }

    FileStorage(Path root) {
        this.root = root;
    }

    Path provide(String... directoryNames) {
        var directory = root;
        for (var directoryName : directoryNames) {
            directory = directory.resolve(directoryName);
        }
        return provide(directory);
    }

    private static Path provide(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        return directory;
    }
}