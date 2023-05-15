package antivoland.jh.linkedin;

import antivoland.jh.Storage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

@Data
@NoArgsConstructor
class Meta {
    private static final Logger LOG = LoggerFactory.getLogger(Meta.class);
    private static final ObjectMapper MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();

    String name;
    String title;
    String summary;
    String author;
    String organization;
    LocalDate date;
    String url;

    void save(Storage storage) {
        var path = storage.article(date, name).resolve("meta.json");
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), this);
        } catch (IOException e) {
            LOG.warn("Failed to save meta", e);
        }
    }

    static Meta load(Path path) {
        try {
            return MAPPER.readValue(path.toFile(), Meta.class);
        } catch (IOException e) {
            LOG.warn("Failed to load meta", e);
            throw new RuntimeException(e);
        }
    }
}