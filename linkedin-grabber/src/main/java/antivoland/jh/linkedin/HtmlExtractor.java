package antivoland.jh.linkedin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.apache.commons.lang3.StringUtils.*;

public class HtmlExtractor {
    private final Document document;

    private HtmlExtractor(String html) {
        document = Jsoup.parse(html);
    }

    static LinkedinOffer extractOffer(String html) {
        return new HtmlExtractor(html).extractOffer();
    }

    LinkedinOffer extractOffer() {
        return new LinkedinOffer()
                .setId(offerId())
                .setCompanyId(companyId())
                .setTitle(title())
                .setDescription(description())
                .setApplicants(applicants())
                .setDate(date());
    }

    private String offerId() {
        return decode(substringAfterLast(substringBefore(offerUrl(), "?"), "/"));
    }

    private String offerUrl() {
        return trim(document
                .select("a[data-tracking-control-name=public_jobs_topcard-title]")
                .attr("href"));
    }

    private String companyId() {
        return decode(substringAfterLast(substringBefore(companyUrl(), "?"), "/"));
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
                .select("*[class*=job-search-card--active] time")
                .attr("datetime")));
    }

    public static String decode(String url) {
        return URLDecoder.decode(url, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        System.out.println(decode("%D1%82%D0%BE%D1%80%D0%B3%D0%BE%D0%B2%D1%8B%D0%B9-%D0%BF%D1%80%D0%B5%D0%B4%D1%81%D1%82%D0%B0%D0%B2%D0%B8%D1%82%D0%B5%D0%BB%D1%8C-%D0%BD%D0%B0-%D1%84%D0%B8%D0%BD%D1%81%D0%BA%D0%BE%D0%BC-%D0%B8-%D1%88%D0%B2%D0%B5%D0%B4%D1%81%D0%BA%D0%BE%D0%BC-%D1%80%D1%8B%D0%BD%D0%BA%D0%B5-at-vecta-design-o%C3%BC-3555208263"));
    }
}