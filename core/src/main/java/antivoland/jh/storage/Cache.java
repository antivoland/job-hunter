package antivoland.jh.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class Cache<ID> {
    private static final Logger LOG = LoggerFactory.getLogger(Cache.class);
    private static final String DEFAULT_DIR = System.getProperty("user.dir") + "/cache";

    final String name;
    private final FileStorage storage;

    public Cache(String name) {
        this(Paths.get(DEFAULT_DIR), name);
    }

    public Cache(Path root, String name) {
        storage = new FileStorage(root);
        this.name = name;
    }

    public String load(ID id) {
        return load(path(id));
    }

    public void save(ID id, String data) {
        save(path(id), data);
    }

    private Path path(ID id) {
        return FileStorage.provide(storage.root.resolve(name)).resolve(id + ".html");
    }

    private static String load(Path path) {
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            LOG.warn("Failed to load data", e);
            return null;
        }
    }

    private static void save(Path path, String data) {
        try {
            Files.write(path, data.getBytes(), CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            LOG.warn("Failed to save data", e);
        }
    }
}