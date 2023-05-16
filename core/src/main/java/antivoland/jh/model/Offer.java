package antivoland.jh.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Offer {
    String id;
    String companyId;
    String title;
    LocalDate date;

    public Offer merge(Offer offer) {
        if (offer == null) return this;
        return new Offer().setId(id).setCompanyId(companyId).setTitle(title).setDate(date);
    }

    public static Offer parse(String html) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}