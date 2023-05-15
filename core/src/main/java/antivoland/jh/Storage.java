package antivoland.jh;

import antivoland.jh.model.Company;
import antivoland.jh.model.Offer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Storage {
    private static final Logger LOG = LoggerFactory.getLogger(Storage.class);
    private static final ObjectMapper MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();
    private static final String DATA_DIR = System.getProperty("user.dir") + "/data";

    public Path root() {
        return provide(Paths.get(DATA_DIR));
    }

    public Company getCompany(String companyId) {
        return load(companyPath(companyId), Company.class);
    }

    public void updateCompany(Company company) {
        var id = company.getId();
        var path = companyPath(id);
        save(path, company.merge(getCompany(id)));
    }

    public Offer getOffer(String companyId) {
        return load(offerPath(companyId), Offer.class);
    }

    public void updateOffer(Offer offer) {
        var id = offer.getId();
        var path = offerPath(id);
        save(path, offer.merge(getOffer(id)));
    }

    private Path companyPath(String companyId) {
        return provide(root().resolve("companies")).resolve(companyId + ".json");
    }

    private Path offerPath(String offerId) {
        return provide(root().resolve("offers")).resolve(offerId + ".json");
    }

    private static Path provide(Path path) {
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                throw new Error(e);
            }
        }
        return path;
    }

    private static <DATA> void save(Path path, DATA data) {
        try {
            MAPPER.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), data);
        } catch (IOException e) {
            LOG.warn("Failed to save data to {}", path, e);
        }
    }

    private static <DATA> DATA load(Path path, Class<DATA> clazz) {
        if (!Files.exists(path)) {
            return null;
        }
        try {
            return MAPPER.readValue(path.toFile(), clazz);
        } catch (IOException e) {
            LOG.warn("Failed to load data from {}", path, e);
            return null;
        }
    }
}