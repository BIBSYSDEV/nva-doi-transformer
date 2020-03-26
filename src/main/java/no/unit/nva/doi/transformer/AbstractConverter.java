package no.unit.nva.doi.transformer;

import java.net.URI;
import java.util.stream.Stream;
import no.unit.nva.model.Organization;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationStatus;

public class AbstractConverter {

    public static final PublicationStatus DEFAULT_NEW_PUBLICATION_STATUS = PublicationStatus.NEW;
    public static final String FAMILY_NAME_GIVEN_NAME_SEPARATOR = ", ";

    protected String toName(String familyName, String givenName) {
        return String.join(FAMILY_NAME_GIVEN_NAME_SEPARATOR, familyName, givenName);
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
