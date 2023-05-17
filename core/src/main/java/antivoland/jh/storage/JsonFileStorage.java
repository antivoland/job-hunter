package antivoland.jh.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

class JsonFileStorage<DATA, ID> extends FileStorage {
    private static final Logger LOG = LoggerFactory.getLogger(JsonFileStorage.class);
    private static final ObjectMapper MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    private final String name;
    private final Class<DATA> clazz;
    private final Function<DATA, ID> resolveId;

    JsonFileStorage(String name, Class<DATA> clazz, Function<DATA, ID> resolveId) {
        this.name = name;
        this.clazz = clazz;
        this.resolveId = resolveId;
    }

    DATA load(ID id) {
        return load(file(id), clazz);
    }

    void save(DATA data) {
        save(file(resolveId.apply(data)), data);
    }

    private Path file(ID id) {
        return provide("data", name).resolve(id + ".json");
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