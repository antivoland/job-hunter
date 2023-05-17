package antivoland.jh.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class HtmlCache<ID> {
    private static final Logger LOG = LoggerFactory.getLogger(HtmlCache.class);

    final String name;
    private final FileStorage storage = new FileStorage();

    public HtmlCache(String name) {
        this.name = name;
    }

    public String load(ID id) {
        return load(file(id));
    }

    public void save(ID id, String data) {
        save(file(id), data);
    }

    private Path file(ID id) {
        return storage.provide("cache", name).resolve(id + ".html");
    }

    private static String load(Path file) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(file));
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