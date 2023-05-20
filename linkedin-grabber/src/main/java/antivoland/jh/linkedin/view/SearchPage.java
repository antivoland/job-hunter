package antivoland.jh.linkedin.view;

import java.util.Random;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;

public class SearchPage {
    private static final int DELAY_MILLIS = 1000;
    private static final int SCROLL_ATTEMPTS = 10;

    public static void search(String location) {
        open("https://www.linkedin.com/jobs/search");

        $("button[data-control-name='ga-cookie.consent.deny.v4']").click();

        $("input[id='job-search-bar-location']").click();
        $("input[id='job-search-bar-location']").clear();
        $("input[id='job-search-bar-location']").sendKeys(location);
        $("button[data-tracking-control-name='public_jobs_jobs-search-bar_base-search-bar-search-submit']").click();

        $("button[data-tracking-control-name='public_jobs_conversion-modal_dismiss']").click();
    }

    public static Stream<Thumbnail> thumbnails() {
        return $$("ul[class='jobs-search__results-list'] li").stream().map(Thumbnail::new);
    }

    public static boolean select(Thumbnail thumbnail) {
        thumbnail.value().click();
        try {
            $("a[data-tracking-control-name='public_jobs_topcard-title'").shouldBe(text(thumbnail.title()));
            sleep(randomDelayMillis());
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    public static boolean scroll() {
        var count = thumbnails().count();
        var attempts = 0;
        while (attempts < SCROLL_ATTEMPTS) {
            executeJavaScript("scroll(0, document.body.scrollHeight);");
            if (thumbnails().count() > count) return true;
            sleep(randomDelayMillis());
            executeJavaScript("scroll(0, 0);");
            sleep(randomDelayMillis());
            ++attempts;
        }
        return false;
    }

    public static String html() {
        return $("html").innerHtml();
    }

    private static int randomDelayMillis() {
        return DELAY_MILLIS + new Random().nextInt(DELAY_MILLIS);
    }
}