package antivoland.jh.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class TextFileStorage<ID> extends FileStorage {
    private static final Logger LOG = LoggerFactory.getLogger(TextFileStorage.class);

    private final String extension;
    private final String[] directoryNames;

    public TextFileStorage(String extension, String... directoryNames) {
        this.extension = extension;
        this.directoryNames = directoryNames;
    }

    public long count() {
        return count(directoryNames);
    }

    public Stream<String> list() {
        return list(directoryNames).map(TextFileStorage::load);
    }

    public String load(ID id) {
        return load(file(id));
    }

    public void save(ID id, String data) {
        save(file(id), data);
    }

    private Path file(ID id) {
        return provide(directoryNames).resolve(id + "." + extension);
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