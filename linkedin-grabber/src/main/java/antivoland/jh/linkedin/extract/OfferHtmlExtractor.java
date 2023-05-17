package antivoland.jh.linkedin.extract;

import antivoland.jh.model.Offer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.*;

public class OfferHtmlExtractor {
    private final Document document;

    public OfferHtmlExtractor(String html) {
        this.document = Jsoup.parse(html);
    }

    public Offer extract() {
        return new Offer()
                .setId(offerId())
                .setCompanyId(companyId())
                .setTitle(title())
                .setDescription(description())
                .setApplicants(applicants())
                .setDate(date());
    }

    private String offerId() {
        return substringAfterLast(substringBefore(offerUrl(), "?"), "/");
    }

    private String offerUrl() {
        return trim(document
                .select("a[data-tracking-control-name=public_jobs_topcard-title]")
                .attr("href"));
    }

    private String companyId() {
        return substringAfterLast(substringBefore(companyUrl(), "?"), "/");
    }

    private String companyUrl() {
        return trim(document
                .select("a[data-tracking-control-name=public_jobs_topcard-org-name]")
                .attr("href"));
    }

    private String title() {
        return trim(document
                .select("a[data-tracking-control-name=public_jobs_topcard-title] h2")
                .text());
    }

    private String description() {
        return trim(document
                .select("div[class*=show-more-less-html]")
                .text());
    }

    private Integer applicants() {
        return Integer.parseInt(trim(document
                .select("span[class*=num-applicants]")
                .text()).replaceAll("[^\\d.]", ""));
    }

    private LocalDate date() {
        return LocalDate.parse(trim(document
                .select("div[class*=job-search-card--active] time")
                .attr("datetime")));
    }
}