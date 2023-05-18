package antivoland.jh;

import antivoland.jh.model.Offer;
import antivoland.jh.storage.JsonFileStorage;
import antivoland.jh.storage.TextFileStorage;

public class Unlabeled {
    public static void main(String[] args) {
        JsonFileStorage<Offer, String> storage = new JsonFileStorage<>(Offer.class);

        storage.list().forEach(offer -> {
            TextFileStorage<String> unlabeled = new TextFileStorage<>("txt", "nlp", "unlabeled", offer.getId());
            unlabeled.save(offer.getId(), offer.getDescription());
        });
    }
}