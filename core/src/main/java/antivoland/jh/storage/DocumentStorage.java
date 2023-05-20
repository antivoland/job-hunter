package antivoland.jh.storage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.nio.file.Path;
import java.util.stream.Stream;

import static java.lang.String.format;

public class DocumentStorage<DOCUMENT> {
    private static final ObjectMapper MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    private final V2FileStorage storage;
    private final Class<DOCUMENT> clazz;

    public DocumentStorage(Path directory, Class<DOCUMENT> clazz) {
        this.storage = new V2FileStorage(directory, "json");
        this.clazz = clazz;
    }

    public long countDocuments() {
        return storage.countFiles();
    }

    public Stream<DOCUMENT> listDocuments() {
        return listDocumentIds().map(this::loadDocument);
    }

    public Stream<String> listDocumentIds() {
        return storage.listFileIds();
    }

    public boolean documentExists(String documentId) {
        return storage.fileExists(documentId);
    }

    public DOCUMENT loadDocument(String documentId) {
        var data = storage.loadFile(documentId);
        try {
            return MAPPER.readValue(data, clazz);
        } catch (JsonProcessingException e) {
            throw new DocumentStorageException(format("Failed to deserialize document '%s'", documentId), e);
        }
    }

    public void saveDocument(String documentId, DOCUMENT document) {
        String data;
        try {
            data = MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(document);
        } catch (JsonProcessingException e) {
            throw new DocumentStorageException(format("Failed to serialize document '%s'", documentId), e);
        }
        storage.saveFile(documentId, data);
    }
}