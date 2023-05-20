package antivoland.jh.linkedin.view;

import antivoland.jh.linkedin.LinkedinOffer;
import antivoland.jh.model.Company;
import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

public record Thumbnail(SelenideElement value) {
    public String title() {
        return trim(value.$("h3").text());
    }

    public String offerId() {
        return substringAfterLast(substringBefore(offerUrl(), "?"), "/");
    }

    String offerUrl() {
        return trim(value.$("a[data-tracking-control-name='public_jobs_jserp-result_search-card']").attr("href"));
    }

    String companyName() {
        return trim(value.$("h4").text());
    }

    String companyId() {
        return substringAfterLast(substringBefore(companyUrl(), "?"), "/");
    }

    String companyUrl() {
        return trim(value.$("h4 a").attr("href"));
    }

    LocalDate date() {
        return LocalDate.parse(trim(value.$("time").attr("datetime")));
    }

    @Deprecated
    LinkedinOffer offer() {
        return new LinkedinOffer().setId(offerId()).setCompanyId(companyId()).setTitle(title()).setDate(date());
    }

    @Override
    public String toString() {
        return "title=" + title() +
                "\nofferId=" + offerId() +
                "\nofferUrl=" + offerUrl() +
                "\ncompanyName=" + companyName() +
                "\ncompanyId=" + companyId() +
                "\ncompanyUrl=" + companyUrl() +
                "\ndate=" + date();
    }
}