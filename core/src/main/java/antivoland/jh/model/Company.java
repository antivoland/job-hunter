package antivoland.jh.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class Company {
    String id;
    Set<String> names;
}