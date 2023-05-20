package antivoland.jh.linkedin;

import ru.creditnet.progressbar.ConsoleProgressBar;

import java.io.IOException;

import static antivoland.jh.Resource.linkedinCache;
import static antivoland.jh.Resource.linkedinOffers;

class Processor {
    boolean shouldProcess(String offerId) {
        return !linkedinOffers().documentExists(offerId);
    }

    void process(String offerId, String html) {
        linkedinCache().saveFile(offerId, html);
        linkedinOffers().saveDocument(offerId, HtmlExtractor.extractOffer(html));
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Reprocessing cache...");
        try (var bar = new ConsoleProgressBar(linkedinCache().countFiles())) {
            linkedinCache().listFiles().map(HtmlExtractor::extractOffer).forEach(offer -> {
                linkedinOffers().saveDocument(offer.getId(), offer);
                bar.step();
            });
        }
    }
}