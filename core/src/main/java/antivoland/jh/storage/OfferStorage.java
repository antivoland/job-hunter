package antivoland.jh.storage;

import antivoland.jh.model.Offer;

public class OfferStorage {
    private final JsonFileStorage<Offer, String> storage;

    public OfferStorage() {
        storage = new JsonFileStorage<>("offers", Offer.class, Offer::getId);
    }

    public Offer get(String id) {
        return storage.load(id);
    }

    public void update(Offer offer) {
        storage.save(offer.merge(get(offer.getId())));
    }
}