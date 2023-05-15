package antivoland.jh;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Storage {
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data";

    public Path root() {
        return provide(Paths.get(DATA_DIR));
    }

    public Path article(LocalDate date, String name) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return provide(Paths.get(DATA_DIR, "articles", date.format(formatter), name));
    }

    public Path articles() {
        return provide(Paths.get(DATA_DIR, "articles"));
    }

    private Path provide(Path path) {
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