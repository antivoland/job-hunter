package antivoland.jh.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.file.Path;
import java.util.stream.Stream;

import static java.lang.String.format;

public class DocumentFileStorage<DOCUMENT> implements Storage<DOCUMENT> {
    private static final ObjectMapper MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    private final TextFileStorage storage;
    private final Class<DOCUMENT> clazz;

    public DocumentFileStorage(Path directory, Class<DOCUMENT> clazz) {
        this.storage = new TextFileStorage(directory, "json");
        this.clazz = clazz;
    }

    @Override
    public Stream<String> listIds() {
        return storage.listIds();
    }

    @Override
    public boolean exists(String id) {
        return storage.exists(id);
    }

    @Override
    public DOCUMENT load(String id) {
        var data = storage.load(id);
        if (data == null) return null;
        try {
            return MAPPER.readValue(data, clazz);
        } catch (JsonProcessingException e) {
            throw new StorageException(format("Failed to deserialize document '%s'", id), e);
        }
    }

    @Override
    public void save(String id, DOCUMENT document) {
        String data;
        try {
            data = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(document);
        } catch (JsonProcessingException e) {
            throw new StorageException(format("Failed to serialize document '%s'", id), e);
        }
        storage.save(id, data);
    }
}