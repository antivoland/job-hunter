package antivoland.jh.linkedin;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LinkedinOffer {
    String id;
    String companyId;
    String title;
    String description;
    Integer applicants;
    LocalDate date;
}