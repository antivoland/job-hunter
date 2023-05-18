package antivoland.jh.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class JsonFileStorage<DATA, ID> extends FileStorage {
    private static final Logger LOG = LoggerFactory.getLogger(JsonFileStorage.class);
    private static final ObjectMapper MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    private final Class<DATA> clazz;

    public JsonFileStorage(Class<DATA> clazz) {
        this.clazz = clazz;
    }

    public boolean exists(ID id) {
        return Files.exists(file(id));
    }

    public Stream<DATA> list() {
        return list("data").map(file -> load(file, clazz));
    }

    public DATA load(ID id) {
        return load(file(id), clazz);
    }

    public void save(ID id, DATA data) {
        save(file(id), data);
    }

    private Path file(ID id) {
        return provide("data").resolve(id + ".json");
    }

    private static <DATA> DATA load(Path file, Class<DATA> clazz) {
        if (!Files.exists(file)) {
            return null;
        }
        try {
            return MAPPER.readValue(file.toFile(), clazz);
        } catch (IOException e) {
            LOG.warn("Failed to load data", e);
            return null;
        }
    }

    private static <DATA> void save(Path file, DATA data) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(file.toFile(), data);
        } catch (IOException e) {
            LOG.warn("Failed to save data", e);
        }
    }
}