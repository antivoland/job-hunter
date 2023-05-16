package antivoland.jh.linkedin;

import antivoland.jh.Storage;
import antivoland.jh.linkedin.search.Thumbnail;
import com.google.inject.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.*;

class Grabber {
    private static final Logger LOG = LoggerFactory.getLogger(Grabber.class);

    public static void main(String[] args) {
        var injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
            @Override
            protected void configure() {
                bind(Storage.class).in(Scopes.SINGLETON);
                bind(Grabber.class).in(Scopes.SINGLETON);
            }
        });

        LOG.info("Data directory: " + injector.getInstance(Storage.class).root());
        injector.getInstance(Grabber.class).run();
    }

    final Storage storage;

    @Inject
    Grabber(Storage storage) {
        this.storage = storage;
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
                .map(Thumbnail::new)
                .forEach(thumbnail -> {
                    storage.updateCompany(thumbnail.company());
                    storage.updateOffer(thumbnail.offer());
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