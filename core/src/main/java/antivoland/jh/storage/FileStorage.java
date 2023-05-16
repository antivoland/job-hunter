package antivoland.jh.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileStorage {
    private static final String DEFAULT_DATA_DIR = System.getProperty("user.dir") + "/data";

    protected final Path root;

    FileStorage() {
        this(Paths.get(DEFAULT_DATA_DIR));
    }

    FileStorage(Path root) {
        this.root = root;
    }

    protected static Path provide(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        return path;
    }
}