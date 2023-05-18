package antivoland.jh.linkedin;

import antivoland.jh.model.Company;
import antivoland.jh.model.Offer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.*;

class HtmlExtractor {
    private final Document document;

    public HtmlExtractor(String html) {
        document = Jsoup.parse(html);
    }

    static Offer extractOffer(String html) {
        return new HtmlExtractor(html).extractOffer();
    }

    Offer extractOffer() {
        return new Offer()
                .setId(offerId())
                .setCompanyId(companyId())
                .setTitle(title())
                .setDescription(description())
                .setApplicants(applicants())
                .setDate(date());
    }

    Company extractCompany() {
        return new Company()
                .setId(offerId())
                .setNames(Set.of(companyName()));
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

    private String companyName() {
        return trim(document
                .select("a[data-tracking-control-name=public_jobs_topcard-org-name]")
                .text());
    }

    private String title() {
        return trim(document
                .select("a[data-tracking-control-name=public_jobs_topcard-title] h2")
                .text());
    }

    private String description() {
        var html = document.select("div[class*=show-more-less-html]").html();
        var description = Jsoup.parse(html);
        description.select("*").append("x_X");
        var lines = description.text()
                .replaceAll("x_X", "\n")
                .replaceAll("\\.", "\\.\n");
        var sb = new StringBuilder();
        for (var line : lines.split("\n")) {
            if (!isBlank(line)) {
                sb.append(trim(line)).append("\n");
            }
        }
        return trim(sb.toString());
    }

    private Integer applicants() {
        return Integer.parseInt(trim(document
                .select("*[class*=num-applicants__caption]")
                .text()).replaceAll("[^\\d.]", ""));
    }

    private LocalDate date() {
        return LocalDate.parse(trim(document
                .select("div[class*=job-search-card--active] time")
                .attr("datetime")));
    }
}