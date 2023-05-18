package antivoland.jh.linkedin;

import antivoland.jh.model.Offer;
import antivoland.jh.storage.HtmlCache;
import antivoland.jh.storage.JsonFileStorage;
import ru.creditnet.progressbar.ConsoleProgressBar;

import java.io.IOException;

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

    public static void main(String[] args) throws IOException {
        System.out.println("Reprocessing cache...");
        var cache = new HtmlCache<>();
        var storage = new JsonFileStorage<>(Offer.class);

        try (var bar = new ConsoleProgressBar(cache.count())) {
            cache.list().map(HtmlExtractor::extractOffer).forEach(offer -> {
                storage.save(offer.getId(), offer);
                bar.step();
            });
        }
    }
}