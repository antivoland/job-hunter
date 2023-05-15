package antivoland.jh.linkedin;

import antivoland.jh.Storage;
import antivoland.jh.model.Company;
import antivoland.jh.model.Offer;
import com.codeborne.selenide.SelenideElement;
import com.google.inject.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Set;

import static com.codeborne.selenide.Selenide.*;
import static org.apache.commons.lang3.StringUtils.*;

class Scroller {
    private static final Logger LOG = LoggerFactory.getLogger(Scroller.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Storage.class).in(Scopes.SINGLETON);
                bind(Scroller.class).in(Scopes.SINGLETON);
            }
        });

        LOG.info("Data directory: " + injector.getInstance(Storage.class).root());
        injector.getInstance(Scroller.class).run("tallinn");
    }

    final Storage storage;

    @Inject
    Scroller(Storage storage) {
        this.storage = storage;
    }

    void run(String city) {
        LOG.info("Scrolling...");
        using(new ChromeDriver(), () -> {
            open("https://www.linkedin.com/jobs/search");

            $("button[data-control-name='ga-cookie.consent.deny.v4']").click();

            $("input[id='job-search-bar-location']").click();
            $("input[id='job-search-bar-location']").clear();
            $("input[id='job-search-bar-location']").sendKeys(city);
            $("button[data-tracking-control-name='public_jobs_jobs-search-bar_base-search-bar-search-submit']").click();

            $("button[data-tracking-control-name='public_jobs_conversion-modal_dismiss']").click();

            $$("ul[class='jobs-search__results-list'] li")
                    .stream()
                    .map(Thumbnail::new)
                    .forEach(thumbnail -> {
                        storage.updateCompany(thumbnail.company());
                        storage.updateOffer(thumbnail.offer());
                    });

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

            closeWindow();
             */
        });
    }

    static void scroll(int times) {
        int i = 0;
        while (i < times) {
            sleep(1000);
            executeJavaScript("scroll(0, document.body.scrollHeight);");
            ++i;
        }
    }

    record Thumbnail(SelenideElement value) {

        String title() {
            return trim(value.$("h3").text());
        }

        String offerUrl() {
            return trim(value.$("a[data-tracking-control-name='public_jobs_jserp-result_search-card']").attr("href"));
        }

        String offerId() {
            return substringAfterLast(substringBefore(offerUrl(), "?"), "/");
        }

        String companyName() {
            return trim(value.$("h4").text());
        }

        String companyUrl() {
            return trim(value.$("h4 a").attr("href"));
        }

        String companyId() {
            return substringAfterLast(substringBefore(companyUrl(), "?"), "/");
        }

        LocalDate date() {
            return LocalDate.parse(trim(value.$("time").attr("datetime")));
        }

        Company company() {
            return new Company().setId(companyId()).setNames(Set.of(companyName()));
        }

        Offer offer() {
            return new Offer().setId(offerId()).setCompanyId(companyId()).setTitle(title()).setDate(date());
        }

        @Override
        public String toString() {
            return "title=" + title() +
                    "\nofferUrl=" + offerUrl() +
                    "\nofferId=" + offerId() +
                    "\ncompanyName=" + companyName() +
                    "\ncompanyUrl=" + companyUrl() +
                    "\ncompanyId=" + companyId() +
                    "\ndate=" + date();
        }
    }
}