package antivoland.jh.linkedin;

import antivoland.jh.linkedin.view.SearchPage;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.atomic.AtomicInteger;

import static antivoland.jh.Resource.linkedinOffers;
import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.using;

class Grabber {
    public static void main(String[] args) {
        Processor processor = new Processor();
        var processed = new AtomicInteger(0);

        using(new ChromeDriver(), () -> {
            SearchPage.search("tallinn");

            do {
                SearchPage.thumbnails()
                        .filter(thumbnail -> processor.shouldProcess(thumbnail.offerId()))
                        .forEach(thumbnail -> {
                            if (SearchPage.select(thumbnail)) {
                                processor.process(thumbnail.offerId(), SearchPage.html());
                                processed.incrementAndGet();
                            }
                        });
            } while (SearchPage.scroll());

            closeWindow();
        });

        System.out.printf("Processed %s new LinkedIn offers (the total amount is now %s)%n",
                processed.get(),
                linkedinOffers().count());
    }
}