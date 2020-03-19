package no.unit.nva.doi.transformer;

import java.util.stream.Stream;
import no.unit.nva.model.PublicationDate;

public abstract class AbstractConverter {

    protected String toName(String familyName, String givenName) {
            return String.join(", ", familyName, givenName);
    }

    protected PublicationDate toDate(Integer publicationYear) {
        return new PublicationDate.Builder()
            .withYear(publicationYear.toString())
            .build();
    }

    protected String getMainTitle(Stream<String> titles) {
        return titles.findFirst().orElse(null);
    }

}
