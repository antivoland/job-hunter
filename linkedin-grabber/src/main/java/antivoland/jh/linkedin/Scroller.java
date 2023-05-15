package antivoland.jh.linkedin;

import antivoland.jh.Storage;
import com.codeborne.selenide.SelenideElement;
import com.google.inject.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.MonthDay;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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
        injector.getInstance(Scroller.class).run("technology");
    }

    final Storage storage;

    @Inject
    Scroller(Storage storage) {
        this.storage = storage;
    }

    void run(String topic) {
        LOG.info("Scrolling...");
        using(new ChromeDriver(), () -> {
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

    record Section(SelenideElement value) {

        String name() {
            var url = value.$("a").attr("href");
            url = substringBefore(url, "?");
            return substringAfterLast(url, "/");
        }

        String title() {
            return value.$("h3").text();
        }

        boolean hasSummary() {
            return value.$$("h3").size() > 1;
        }

        String summary() {
            return hasSummary() ? value.$$("h3").get(1).text() : null;
        }

        String author() {
            var links = value.$$("a");
            var url = links.get(hasSummary() ? 3 : 2).attr("href");
            url = substringBefore(url, "?");
            if (url.contains("@")) {
                return substringAfter(url, "@");
            }
            return substringBetween(url, "//", ".");
        }

        String organization() {
            var links = value.$$("a");
            var link = links.get(hasSummary() ? 4 : 3);
            if (!link.text().startsWith(" in ")) return null;
            var url = link.attr("href");
            url = substringBefore(url, "?");
            return substringAfter(url, ".com/");
        }

        LocalDate date() {
            var values = value.text().split("\n");
            var monthDay = values[values.length - 3];
            var formatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH);
            return MonthDay.parse(monthDay, formatter).atYear(Year.now().getValue());
        }

        String url() {
            var url = value.$("a").attr("href");
            return substringBefore(url, "?");
        }

        Meta asMeta() {
            return new Meta()
                    .setName(name())
                    .setTitle(title())
                    .setSummary(summary())
                    .setAuthor(author())
                    .setOrganization(organization())
                    .setDate(date())
                    .setUrl(url());
        }

        static boolean suitable(SelenideElement value) {
            return value.$$("a").size() > 3;
        }
    }
}