package antivoland.jh.linkedin;

import antivoland.jh.Storage;
import antivoland.jh.model.Offer;
import com.codeborne.selenide.SelenideElement;
import com.google.inject.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;

import static com.codeborne.selenide.Selenide.*;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;

class Grabber {
    /*
    private static final Logger LOG = LoggerFactory.getLogger(Grabber.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(Stage.PRODUCTION, new AbstractModule() {
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
        LOG.info("Grabbing...");
        try {
            Files.walk(storage.articles())
                    .sorted(reverseOrder())
                    .filter(Files::isRegularFile)
                    .filter(path -> path.endsWith("meta.json"))
                    .peek(path -> LOG.debug(path.toString()))
                    .map(Offer::load)
                    .forEach(this::grab);
        } catch (IOException e) {
            LOG.warn("Failed to grab articles");
        }
    }

    void grab(Offer offer) {
        var path = storage.article(offer.date, offer.name).resolve("article.txt");
        if (Files.exists(path)) {
            return;
        }
        using(new ChromeDriver(), () -> {
            open(offer.url);

            var text = $$("section")
                    .stream()
                    .map(SelenideElement::text)
                    .max(comparing(String::length))
                    .orElseThrow();
            try {
                Files.write(path, text.getBytes());
            } catch (IOException e) {
                LOG.warn("Failed to save article");
            }

            closeWindow();
        });
    }
     */
}