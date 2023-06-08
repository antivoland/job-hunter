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
            // process("staff software engineer", "tallinn");
            process("senior software engineer", "tallinn");
            // process("software engineer", "tallinn");
            // process("data engineer", "tallinn");
            // process("data scientist", "tallinn");
            // process("data analyst", "tallinn");
            // process("engineering manager", "tallinn");
            // process("project manager", "tallinn");
            // process("backend", "tallinn");
            // process("frontend", "tallinn");
            // process("java", "tallinn");
            // process("scala", "tallinn");
            // process("python", "tallinn");
            // process("aws", "tallinn");
            // process("azure", "tallinn");

            // process("fintech", "tallinn");

            // process("visa sponsorship", "tallinn");
            // process("relocation", "tallinn");
            // process("residence permit", "tallinn");

            // process("quantum", "tallinn");
            // process("science", "tallinn");

            // process("senior software engineer", "stockholm");

            // process("senior software engineer", "amsterdam");

            // process("senior software engineer", "berlin");

            // process("senior software engineer", "london");

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

        System.out.printf("Processed %s new LinkedIn offers (the total amount was %s)%n",
                processed.get(),
                SearchPage.thumbnails().count());
    }
}