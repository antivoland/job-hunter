package antivoland.jh;

public class Unlabeled {
    public static void main(String[] args) {
        var offers = Resource.linkedinOffers();

        offers.list().forEach(offer -> {
            var unlabeled = Resource.textFiles("nlp", "unlabeled", offer.getId());
            unlabeled.save(offer.getId(), offer.getDescription());
        });
    }
}