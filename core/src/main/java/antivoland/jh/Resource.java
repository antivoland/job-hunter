package antivoland.jh;

import antivoland.jh.linkedin.LinkedinOffer;
import antivoland.jh.storage.DocumentStorage;
import antivoland.jh.storage.HtmlCache;
import antivoland.jh.storage.TextFileStorage;
import com.google.common.base.Suppliers;

import java.nio.file.Paths;
import java.util.function.Supplier;

public class Resource {
    private static final String DEFAULT_ROOT = System.getProperty("user.dir");

    private static final Supplier<DocumentStorage<LinkedinOffer>> LINKEDIN_OFFERS = Suppliers.memoize(() ->
            new DocumentStorage<>(Paths.get(DEFAULT_ROOT, "data", "linkedin", "offers"), LinkedinOffer.class));

    private static final Supplier<HtmlCache> LINKEDIN_CACHE = Suppliers.memoize(() ->
            new HtmlCache(Paths.get(DEFAULT_ROOT, "data", "linkedin", "cache")));

    public static DocumentStorage<LinkedinOffer> linkedinOffers() {
        return LINKEDIN_OFFERS.get();
    }

    public static HtmlCache linkedinCache() {
        return LINKEDIN_CACHE.get();
    }

    public static TextFileStorage textFiles(String... relativePath) {
        return new TextFileStorage(Paths.get(DEFAULT_ROOT, relativePath), "txt");
    }
}