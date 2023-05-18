package antivoland.jh.linkedin;

import antivoland.jh.model.Offer;
import antivoland.jh.storage.JsonFileStorage;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Scopes;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.lang.String.format;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;

public class Insights {
    public static void main(String[] args) {
        var injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Processor.class).in(Scopes.SINGLETON);
                bind(Insights.class).in(Scopes.SINGLETON);
            }
        });

        injector.getInstance(Insights.class).run();

    }

    final JsonFileStorage<Offer, String> storage = new JsonFileStorage<>(Offer.class);

    void run() {
        storage.list()
                .sorted(comparing(Offer::getApplicants))
                .forEach(offer -> System.out.println(format("%s, %s", offer.getTitle(), offer.getApplicants())));

        System.out.println("==============================================");

        var grouped = storage.list().collect(groupingBy(Offer::getCompanyId));
        grouped.entrySet().stream()
                .map(e -> new Company()
                        .setId(e.getKey())
                        .setOpenings(e.getValue().size())
                        .setApplicants((float) e.getValue().stream().mapToInt(Offer::getApplicants).sum() / e.getValue().size()))
                .sorted(comparing(Company::getOpenings).thenComparing(Company::getApplicants))
//                .sorted(comparing(Company::getApplicants).thenComparing(Company::getOpenings))
                .forEach(System.out::println);


//        Map<String, Company> companies = new HashMap<>();
//        storage.list().forEach(offer -> {
//            var company = companies.getOrDefault(offer.getCompanyId(), new Company(offer.getCompanyId()));
//            ++company.openings;
//            company.applicants += offer.getApplicants();
//            companies.put(company.getId(), company);
//        });
//
//        companies.values().stream()
//                .sorted(comparing(Company::getApplicants).thenComparing(Company::getOpenings))
//                .forEach(System.out::println);
//
    }

    @Data
    @NoArgsConstructor
    static class Company {
        String id;
        long openings;
        float applicants;
    }
}