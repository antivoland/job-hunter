package antivoland.jh.linkedin;

import antivoland.jh.Resource;
import antivoland.jh.storage.DocumentFileStorage;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Scopes;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.TreeMap;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

public class Insights {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Insights.class).in(Scopes.SINGLETON);
            }
        });

        injector.getInstance(Insights.class).run();
    }

    final DocumentFileStorage<LinkedinOffer> storage = Resource.linkedinOffers();

    void run() {
        storage.list()
                .sorted(comparing(LinkedinOffer::getApplicants))
                .forEach(offer -> System.out.println(format("%s, %s", offer.getTitle(), offer.getApplicants())));

        System.out.println("==============================================");

        var grouped = storage.list().collect(groupingBy(LinkedinOffer::getCompanyId));
        grouped.entrySet().stream()
                .map(e -> {
                    var offers = e.getValue();
                    var dates = offers.stream().collect(groupingBy(LinkedinOffer::getDate, () -> new TreeMap<>(reverseOrder()), counting()));//.map(Offer::getDate).collect(toSet()).stream().sorted(reverseOrder()).toList();
                    return new Company()
                            .setId(e.getKey())
                            .setOpenings(offers.size())
                            .setApplicants((float) offers.stream().mapToInt(LinkedinOffer::getApplicants).sum() / offers.size())
                            .setDates(dates);
                })

                .sorted(comparing(Company::getOpenings).thenComparing(Company::getApplicants))
//                .sorted(comparing(Company::getApplicants).thenComparing(Company::getOpenings))
                .forEach(System.out::println);

    }

    @Data
    @NoArgsConstructor
    static class Company {
        String id;
        long openings;
        float applicants;
        TreeMap<LocalDate, Long> dates;
    }
}