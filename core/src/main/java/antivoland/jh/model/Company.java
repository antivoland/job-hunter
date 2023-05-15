package antivoland.jh.model;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class Company {
    String id;
    Set<String> names;

    public Company merge(Company company) {
        if (company == null) return this;
        return new Company().setId(id).setNames(Sets.union(names, company.names));
    }
}