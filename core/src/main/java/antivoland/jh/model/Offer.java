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
    String description;
    Integer applicants;
    LocalDate date;
}