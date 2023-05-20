package antivoland.jh.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.lang.String.format;

@Deprecated
class V2FileStorage {

    protected static String fileId(Path file, String fileExtension) {
        return fileId(fileName(file), fileExtension);
    }

    protected static String fileId(String fileName, String fileExtension) {
        return fileName.substring(0, fileName.length() - fileExtension.length() - 1);
    }

    protected static String fileName(Path file) {
        return file.getFileName().toString();
    }

    protected static String fileName(String fileId, String fileExtension) {
        return fileId + "." + fileExtension;
    }

    protected static Path provideDirectory(Path directory) {
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                throw new StorageException(format("Failed to create directory '%s'", directory), e);
            }
        }
        return directory;
    }
}