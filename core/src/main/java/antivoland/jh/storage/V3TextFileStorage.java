package antivoland.jh.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class V3TextFileStorage extends V3FileStorage<String> {

    public V3TextFileStorage(Path directory, String fileExtension) {
        super(directory, fileExtension);
    }

    @Override
    public String load(String id) {
        var file = file(id);
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new StorageException(format("Failed to load file '%s'", file), e);
        }
    }

    @Override
    public void save(String id, String data) {
        var file = file(id, true);
        try {
            Files.write(file, data.getBytes(), CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(format("Failed to save file '%s'", file), e);
        }

    }
}