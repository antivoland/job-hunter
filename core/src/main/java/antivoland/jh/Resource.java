package antivoland.jh;

import antivoland.jh.linkedin.LinkedinOffer;
import antivoland.jh.storage.DocumentStorage;
import antivoland.jh.storage.V2FileStorage;
import com.google.common.base.Suppliers;

import java.nio.file.Paths;
import java.util.function.Supplier;

public class Resource {
    private static final String DEFAULT_ROOT = System.getProperty("user.dir");

    private static final Supplier<DocumentStorage<LinkedinOffer>> LINKEDIN_OFFERS = Suppliers.memoize(() ->
            new DocumentStorage<>(Paths.get(DEFAULT_ROOT, "data", "linkedin", "offers"), LinkedinOffer.class));

    private static final Supplier<V2FileStorage> LINKEDIN_CACHE = Suppliers.memoize(() ->
            new V2FileStorage(Paths.get(DEFAULT_ROOT, "data", "linkedin", "cache"), "html"));

    public static DocumentStorage<LinkedinOffer> linkedinOffers() {
        return LINKEDIN_OFFERS.get();
    }

    public static V2FileStorage linkedinCache() {
        return LINKEDIN_CACHE.get();
    }
}