package antivoland.jh.linkedin;

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
                bind(Processor.class).in(Scopes.SINGLETON);
                bind(Grabber.class).in(Scopes.SINGLETON);
            }
        });

        injector.getInstance(Grabber.class).run();
    }

    final Processor processor;

    @Inject
    Grabber(Processor processor) {
        this.processor = processor;
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
                    if (processor.shouldProcess(thumbnail.offerId())) {
                        sleep(1000 + new Random().nextInt(1000));
                        thumbnail.value().click();

                        try {
                            $("a[data-tracking-control-name='public_jobs_topcard-title'")
                                    .shouldBe(text(thumbnail.offer().getTitle()));

                            processor.process(thumbnail.offerId(), $("html").innerHtml());
                        } catch (AssertionError e) {
                            // todo: implement some response
                        }
                    }
                });
    }

    public void scroll() {
        executeJavaScript("scroll(0, document.body.scrollHeight);");
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