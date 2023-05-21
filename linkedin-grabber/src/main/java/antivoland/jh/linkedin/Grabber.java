package antivoland.jh.linkedin;

import antivoland.jh.linkedin.view.SearchPage;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static antivoland.jh.Resource.*;
import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.using;
import static org.apache.commons.lang3.StringUtils.isBlank;

class Grabber {
    public static void main(String[] args) {
        using(new ChromeDriver(), () -> {
            // process("senior software engineer", "tallinn");
            process("java", "tallinn");

            // process("senior software engineer", "stockholm");

            // process(null, "amsterdam");

            closeWindow();
        });
    }

    static void process(String query, String location) {
        var processed = new AtomicInteger(0);

        SearchPage.search(query, location);
        do {
            SearchPage.thumbnails().forEach(thumbnail -> {
                var id = thumbnail.offerId();
                if (!isBlank(query)) {
                    var context = new LinkedinOfferContext()
                            .setId(id)
                            .setQueries(Set.of(query))
                            .setLocations(Set.of(location));
                    context = context.merge(linkedinContexts().load(id));
                    linkedinContexts().save(id, context);
                }

                if (linkedinOffers().exists(id)) return;
                if (!SearchPage.select(thumbnail)) return;
                var html = SearchPage.html();
                linkedinCache().save(id, html);
                linkedinOffers().save(id, HtmlExtractor.extractOffer(html));

                processed.incrementAndGet();
            });
        } while (SearchPage.scroll());

        System.out.printf("Processed %s new LinkedIn offers (the total amount is now %s)%n",
                processed.get(),
                linkedinOffers().count());
    }
}