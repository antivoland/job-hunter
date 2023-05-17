package antivoland.jh.linkedin;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

class HtmlExtractorTest {
    @Test
    void test() {
        var html = load("cache/marketing-assistant.html");
        // var html = load("cache/specjalista-epr.html");
        var offer = new HtmlExtractor(html).extractOffer();
        var company = new HtmlExtractor(html).extractCompany();
        System.out.println(offer);
        System.out.println(company);
        // todo: compare extracted offer and company with samples
    }

    @SneakyThrows
    private static String load(String path) {
        try (var in = HtmlExtractorTest.class.getClassLoader().getResourceAsStream(path)) {
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}