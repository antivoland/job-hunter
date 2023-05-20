package antivoland.jh.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

@Deprecated
public class V2TextFileStorage extends V2FileStorage {
    private final Path directory;
    private final String fileExtension;

    public V2TextFileStorage(Path directory, String fileExtension) {
        this.directory = directory;
        this.fileExtension = fileExtension;
    }

    public long countFiles() {
        if (!Files.exists(directory)) {
            return 0;
        }
        try (var files = Files.walk(directory).filter(Files::isRegularFile).filter(this::hasRequiredExtension)) {
            return files.count();
        } catch (IOException e) {
            throw new StorageException(format("Failed to count files in directory '%s'", directory), e);
        }
    }

    public Stream<String> listFiles() {
        return listFileIds().map(this::loadFile);
    }

    public Stream<String> listFileIds() {
        if (!Files.exists(directory)) {
            return Stream.empty();
        }
        try (var files = Files.walk(directory).filter(Files::isRegularFile).filter(this::hasRequiredExtension)) {
            return files.map(this::fileId).toList().stream();
        } catch (IOException e) {
            throw new StorageException(format("Failed to traverse files in directory '%s'", directory), e);
        }
    }

    public boolean fileExists(String fileId) {
        return Files.exists(directory.resolve(fileName(fileId)));
    }

    public String loadFile(String fileId) {
        return loadFile(directory.resolve(fileName(fileId)));
    }

    public void saveFile(String fileId, String data) {
        saveFile(provideDirectory(directory).resolve(fileName(fileId)), data);
    }

    private boolean hasRequiredExtension(Path file) {
        return fileName(file).toLowerCase().endsWith("." + fileExtension.toLowerCase());
    }

    private String fileId(Path file) {
        return fileId(file, fileExtension);
    }

    private String fileId(String fileName) {
        return fileId(fileName, fileExtension);
    }

    private String fileName(String fileId) {
        return fileName(fileId, fileExtension);
    }

    private static String loadFile(Path file) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(file));
        } catch (IOException e) {
            throw new StorageException(format("Failed to load file '%s'", file), e);
        }
    }

    private static void saveFile(Path file, String data) {
        try {
            Files.write(file, data.getBytes(), CREATE, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new StorageException(format("Failed to save file '%s'", file), e);
        }
    }
}