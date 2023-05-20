package antivoland.jh.linkedin;

import antivoland.jh.model.Company;
import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

record Thumbnail(SelenideElement value) {
    String title() {
        return trim(value.$("h3").text());
    }

    String offerId() {
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

    LinkedinOffer offer() {
        return new LinkedinOffer().setId(offerId()).setCompanyId(companyId()).setTitle(title()).setDate(date());
    }

    Company company() {
        return new Company().setId(companyId()).setNames(Set.of(companyName()));
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