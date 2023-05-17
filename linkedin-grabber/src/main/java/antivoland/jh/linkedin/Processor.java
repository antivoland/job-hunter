package antivoland.jh.linkedin;

import antivoland.jh.model.Offer;
import antivoland.jh.storage.HtmlCache;
import antivoland.jh.storage.JsonFileStorage;

class Processor {
    private final JsonFileStorage<Offer, String> storage = new JsonFileStorage<>(Offer.class);
    private final HtmlCache<String> cache = new HtmlCache<>();

    boolean shouldProcess(String id) {
        return !storage.exists(id);
    }

    void process(String id, String html) {
        cache.save(id, html);
        storage.save(id, new HtmlExtractor(html).extractOffer());
    }
}