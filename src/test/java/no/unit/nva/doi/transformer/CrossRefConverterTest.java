package no.unit.nva.doi.transformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Collections;
import java.util.UUID;
import no.unit.nva.doi.transformer.model.crossrefmodel.Author;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossRefDocument;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossrefDate;
import no.unit.nva.model.Publication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CrossRefConverterTest {

    private static final Instant NOW = Instant.now();
    public static final String AUTHOR_GIVEN_NAME = "givenName";
    public static final String AUTHOR_FAMILY_NAME = "familyName";
    public static final String FIRST_AUTHOR = "first";
    private static final int DATE_SIZE = 3;
    private static final int NUMBER_OF_DATES = 2;
    private final UUID DOCID = UUID.randomUUID();
    private final String OWNER = "TheOwner";


    private CrossRefDocument sampleDocument= createSampleDocument();

    private CrossRefDocument createSampleDocument() {
        CrossRefDocument document= new CrossRefDocument();
        setAuthor(document);


    }


    private setPublicationDate(CrossRefDocument document){
        int[][] dateParts= new int[NUMBER_OF_DATES][DATE_SIZE];
        int expectedYear=2018;
        int notExpectedYea=2019
        dateParts[0]= new int[] {2019,2,20};
        dateParts[1]= new int[] {2018,2,20};
        document.setPublishedPrint(new CrossrefDate().set);
    }


    private CrossRefDocument setAuthor(CrossRefDocument document) {
        Author author= new Author.Builder()
            .withGivenName(AUTHOR_GIVEN_NAME)
            .withFamilyName(AUTHOR_FAMILY_NAME)
            .withSequence(FIRST_AUTHOR)
            .build();
        document.setAuthor(Collections.singletonList(author));
        return document;
    }

    @Test
    @DisplayName("An empty CrossRef document throws IllegalArgument exception")
    public void anEmptyCrossRefDocumentThrowsIllegalArgumentException() throws IllegalArgumentException {
        CrossRefDocument doc = new CrossRefDocument();
        CrossRefConverter converter = new CrossRefConverter();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> toPublication(converter, doc));
        assertThat(exception.getMessage(), is(CrossRefConverter.MISSING_PUBLICATION_YEAR_ERROR));
    }

    @Test
    @DisplayName("An empty CrossRef document throws IllegalArgument exception")
    public void anEmptyCrossRefDocumentThrowsIllegalArgumentException() throws IllegalArgumentException {
        CrossRefDocument doc = new CrossRefDocument();
        Author author= new Author.Builder()
            .withFamilyName(AUTHOR_FAMILY_NAME)
            .withGivenName(AUTHOR_GIVEN_NAME)
            .withSequence(FIRST_AUTHOR)
            .build();
        doc.setAuthor(Collections.singletonList(author));
        CrossRefConverter converter = new CrossRefConverter();

    }


    private Publication toPublication(CrossRefConverter converter, CrossRefDocument doc) {
        return converter.toPublication(doc, NOW, OWNER, DOCID);
    }
}
