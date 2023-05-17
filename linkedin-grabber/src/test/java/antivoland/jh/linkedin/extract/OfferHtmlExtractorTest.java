package antivoland.jh.linkedin.extract;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

class OfferHtmlExtractorTest {
    @Test
    void test() {
        var html = load("cache/offers/specjalista-epr-at-blrt-grupp-3591706064.html");
        var offer = new OfferHtmlExtractor(html).extract();
        System.out.println(offer);
        // todo: compare extracted offer with sample
    }

    @SneakyThrows
    private static String load(String path) {
        try (var in = OfferHtmlExtractorTest.class.getClassLoader().getResourceAsStream(path)) {
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}