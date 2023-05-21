package antivoland.jh.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public class TextFileStorage extends FileStorage<String> {

    public TextFileStorage(Path directory, String fileExtension) {
        super(directory, fileExtension);
    }

    @Override
    public String load(String id) {
        var file = file(id);
        if (!exists(file)) return null;
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