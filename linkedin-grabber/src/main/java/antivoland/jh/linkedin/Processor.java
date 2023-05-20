package antivoland.jh.linkedin;

import ru.creditnet.progressbar.ConsoleProgressBar;

import java.io.IOException;

import static antivoland.jh.Resource.linkedinCache;
import static antivoland.jh.Resource.linkedinOffers;

class Processor {
    boolean shouldProcess(String offerId) {
        return !linkedinOffers().exists(offerId);
    }

    void process(String offerId, String html) {
        linkedinCache().save(offerId, html);
        linkedinOffers().save(offerId, HtmlExtractor.extractOffer(html));
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Reprocessing cache...");
        try (var bar = new ConsoleProgressBar(linkedinCache().count())) {
            linkedinCache().list().map(HtmlExtractor::extractOffer).forEach(offer -> {
                linkedinOffers().save(offer.getId(), offer);
                bar.step();
            });
        }
    }
}