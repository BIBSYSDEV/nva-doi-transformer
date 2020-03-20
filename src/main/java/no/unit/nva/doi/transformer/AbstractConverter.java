package no.unit.nva.doi.transformer;

import java.net.URI;
import java.util.stream.Stream;
import no.unit.nva.model.Organization;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationStatus;

public class AbstractConverter {

    public static final PublicationStatus DEFAULT_NEW_PUBLICATION_STATUS = PublicationStatus.NEW;

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

    protected Organization toPublisher(URI publisherId) {
        return new Organization.Builder()
            .withId(publisherId)
            .build();
    }
}
