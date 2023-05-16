package antivoland.jh.linkedin;

import antivoland.jh.linkedin.search.Thumbnail;
import antivoland.jh.model.Offer;
import antivoland.jh.storage.Cache;
import antivoland.jh.storage.CompanyStorage;
import antivoland.jh.storage.OfferStorage;
import com.google.inject.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.Random;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

class Grabber {
    public static void main(String[] args) {
        var injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(OfferStorage.class).in(Scopes.SINGLETON);
                bind(CompanyStorage.class).in(Scopes.SINGLETON);
                bind(Grabber.class).in(Scopes.SINGLETON);
            }
        });

        injector.getInstance(Grabber.class).run();
    }

    final OfferStorage offerStorage;
    final CompanyStorage companyStorage;
    final Cache<String> offerCache = new Cache<>("offers");

    @Inject
    Grabber(OfferStorage offerStorage, CompanyStorage companyStorage) {
        this.offerStorage = offerStorage;
        this.companyStorage = companyStorage;
    }

    void run() {
        using(new ChromeDriver(), () -> {
            open("https://www.linkedin.com/jobs/search");

            $("button[data-control-name='ga-cookie.consent.deny.v4']").click();

            $("input[id='job-search-bar-location']").click();
            $("input[id='job-search-bar-location']").clear();
            $("input[id='job-search-bar-location']").sendKeys("tallinn");
            $("button[data-tracking-control-name='public_jobs_jobs-search-bar_base-search-bar-search-submit']").click();

            $("button[data-tracking-control-name='public_jobs_conversion-modal_dismiss']").click();

            new Controller().listen(this);

            /*
            open("https://medium.com/topic/" + topic);

            scroll(0);

            $$("section section")
                    .stream()
                    .peek(section -> LOG.debug(section.innerHtml()))
                    .filter(Section::suitable)
                    .map(Section::new)
                    .map(Section::asMeta)
                    .forEach(meta -> meta.save(storage));

             */

            closeWindow();
        });
    }

    public void grab() {
        $$("ul[class='jobs-search__results-list'] li")
                .stream()
                .limit(3)
                .map(Thumbnail::new)
                .forEach(thumbnail -> {
                    sleep(1000 + new Random().nextInt(1000));
                    thumbnail.value().click();

                    $("a[data-tracking-control-name='public_jobs_topcard-title'")
                            .shouldBe(text(thumbnail.offer().getTitle()));
//                    var details = $("section[class='two-pane-serp-page__detail-view']").innerHtml();
//                    var details = $("html").innerHtml();
                    offerCache.save(thumbnail.offer().getId(), $("html").innerHtml());

                    var cachedOffer = offerCache.load(thumbnail.offer().getId());
                    var offer = Offer.parse(cachedOffer);

                    offerStorage.update(offer);
                    companyStorage.update(thumbnail.company());
                });
    }

    public void scroll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    static void scroll(int times) {
        int i = 0;
        while (i < times) {
            sleep(1000);
            executeJavaScript("scroll(0, document.body.scrollHeight);");
            ++i;
        }
    }
}