package antivoland.jh.linkedin;

import com.google.common.collect.Sets;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class LinkedinOfferContext {
    String id;
    Set<String> queries;
    Set<String> locations;

    public LinkedinOfferContext merge(LinkedinOfferContext context) {
        if (context == null) return this;
        return new LinkedinOfferContext()
                .setId(id)
                .setQueries(Sets.union(queries, context.queries))
                .setLocations(Sets.union(locations, context.locations));
    }
}