package antivoland.jh.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

class FileStorage {
    private static final Path DEFAULT_ROOT = Paths.get(System.getProperty("user.dir"));

    protected final Path root;

    FileStorage() {
        this(DEFAULT_ROOT);
    }

    FileStorage(Path root) {
        this.root = root;
    }

    Stream<Path> list(String... directoryNames) {
        try (var files = Files.walk(provide(directoryNames)).filter(Files::isRegularFile)) {
            return files.toList().stream();
        } catch (IOException e) {
            throw new Error(e);
        }
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