package antivoland.jh.linkedin.view;

import com.codeborne.selenide.SelenideElement;

import java.util.Random;
import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class SearchPage {
    private static final int DELAY_MILLIS = 1000;
    private static final int SCROLL_ATTEMPTS = 5;

    public static void search(String query, String location) {
        open("https://www.linkedin.com/jobs/search");

        $("button[data-control-name='ga-cookie.consent.deny.v4']").click();

        if (!isBlank(query)) {
            query().click();
            query().clear();
            query().sendKeys(query);
        }

        if (!isBlank(location)) {
            location().click();
            location().clear();
            location().sendKeys(location);
        }

        $("button[data-tracking-control-name='public_jobs_jobs-search-bar_base-search-bar-search-submit']").click();

        $("button[data-tracking-control-name='public_jobs_conversion-modal_dismiss']").click();
    }

    private static SelenideElement location() {
        return $("input[id='job-search-bar-location']");
    }

    private static SelenideElement query() {
        return $("input[id='job-search-bar-keywords']");
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
            sleep(randomDelayMillis());

            if ($("button[data-tracking-control-name='infinite-scroller_show-more']").isDisplayed()) {
                $("button[data-tracking-control-name='infinite-scroller_show-more']").click();
                sleep(randomDelayMillis());
                if (thumbnails().count() > count) return true;
            } else {
                if (thumbnails().count() > count) return true;
                executeJavaScript("scroll(0, 0);");
                sleep(randomDelayMillis());
            }

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