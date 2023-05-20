package antivoland.jh;

import antivoland.jh.linkedin.LinkedinOffer;
import antivoland.jh.storage.DocumentStorage;
import antivoland.jh.storage.TextFileStorage;

import java.util.Arrays;
import java.util.List;

public class Labeled {
    enum Language {
        ESTONIAN("""
                %C3%A4mmaemand-%C3%B5de-at-cv-keskus-3603199357
                %C3%A4riarendaja-at-cv-keskus-3603198296
                %C3%A4rijuht-at-lumi-capital-3604833485
                andmearhitekt-at-elering-3601888534
                """),
        RUSSIAN("""
                %D0%BF%D0%BE%D0%B2%D0%B0%D1%80-at-intrezo-o%C3%BC-3600975397
                """),
        ENGLISH("""
                %F0%9F%87%AB%F0%9F%87%B7-business-developer-junior-remote-ue-%F0%9F%87%AB%F0%9F%87%B7-fluent-at-paradox-institute-3595563478
                accountant-at-creditstar-group-3604133458
                accountant-banking-as-a-service-at-monese-3430039789
                """);

        final List<String> ids;

        Language(String ids) {
            this.ids = Arrays.stream(ids.split("\n")).toList();
        }
    }

    public static void main(String[] args) {
        DocumentStorage<LinkedinOffer> storage = Resource.linkedinOffers();

        Arrays.stream(Language.values()).forEach(label -> {
            TextFileStorage labeled = Resource.textFiles("nlp", "labeled", label.name().toLowerCase());
            label.ids.forEach(id -> {
                var offer = storage.load(id);
                labeled.save(id, offer.getDescription());
            });
        });
    }
}