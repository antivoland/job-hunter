package antivoland.jh.model;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.Scanner;

public class OfferTest {
    @Test
    void test() {
        var cached = load("cache/offers/specjalista-epr-at-blrt-grupp-3591706064.html");
//        var cached = load("1.html");
        var parsed = Offer.parse(cached); // todo: parse it
    }

    @SneakyThrows
    private static String load(String path) {
        try (var in = OfferTest.class.getClassLoader().getResourceAsStream(path)) {
            Scanner s = new Scanner(in).useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }
}