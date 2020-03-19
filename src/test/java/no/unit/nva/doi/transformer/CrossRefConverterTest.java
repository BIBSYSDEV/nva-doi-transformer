package no.unit.nva.doi.transformer;

import static no.unit.nva.doi.transformer.CrossRefConverter.ordinals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import no.unit.nva.doi.transformer.model.crossrefmodel.Author;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossRefDocument;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossrefDate;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CrossRefConverterTest {

    private static final Instant NOW = Instant.now();
    public static final String AUTHOR_GIVEN_NAME = "givenName";
    public static final String AUTHOR_FAMILY_NAME = "familyName";
    public static final String FIRST_AUTHOR = "first";
    private static final int DATE_SIZE = 3;
    private static final int NUMBER_OF_DATES = 2;
    public static final Integer EXPECTED_YEAR = 2019;
    public static final int UNEXPECTED_YEAR = EXPECTED_YEAR + 1;
    private static final String SAMPLE_DOCUMENT_TITLE = "Sample document title";
    private static final Integer NUMBER_OF_SAMPLE_AUTHORS = 1;
    public static final String SURNAME_COMMA_FIRSTNAME = "%s,.*%s";
    public static final String NOT_JOURNAL_ARTICLE = "book";
    private final UUID DOCID = UUID.randomUUID();
    private final String OWNER = "TheOwner";

    private CrossRefDocument sampleInputDocument = createSampleDocument();
    private final CrossRefConverter converter = new CrossRefConverter();
    private Publication samplePublication;

    @BeforeEach
    public void init() {
        sampleInputDocument = createSampleDocument();
        samplePublication = toPublication(sampleInputDocument);
    }

    @Test
    @DisplayName("An empty CrossRef document throws IllegalArgument exception")
    public void anEmptyCrossRefDocumentThrowsIllegalArgumentException() throws IllegalArgumentException {
        CrossRefDocument doc = new CrossRefDocument();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> toPublication(doc));
        assertThat(exception.getMessage(), is(CrossRefConverter.INVALID_ENTRY_ERROR));
    }

    @Test
    @DisplayName("The creator's name in the publication contains first family and then given name")
    public void creatorsNameContainsFirstFamilyAndThenGivenName() throws IllegalArgumentException {

        List<Contributor> contributors = samplePublication.getEntityDescription().getContributors();

        String actualName = contributors.get(NUMBER_OF_SAMPLE_AUTHORS - 1).getIdentity().getName();
        String givenName = sampleInputDocument.getAuthor().get(NUMBER_OF_SAMPLE_AUTHORS - 1).getGivenName();
        String familyName = sampleInputDocument.getAuthor().get(NUMBER_OF_SAMPLE_AUTHORS - 1).getFamilyName();
        String expectedNameRegEx = String.format(SURNAME_COMMA_FIRSTNAME, familyName, givenName);

        assertThat(contributors.size(), is(equalTo(sampleInputDocument.getAuthor().size())));
        assertThat(contributors.size(), is(equalTo(NUMBER_OF_SAMPLE_AUTHORS)));
        assertThat(actualName, containsString(givenName));
        assertThat(actualName, containsString(familyName));
        assertThat(actualName, matchesPattern(expectedNameRegEx));
    }

    @Test
    @DisplayName("The creators sequence is translated from ordinal string to number")
    public void creatorsOrdinalStringIsTransformedToNumber() {
        assertThat(sampleInputDocument.getAuthor().size(), is(equalTo(NUMBER_OF_SAMPLE_AUTHORS)));
        String sequenceString = sampleInputDocument.getAuthor().get(NUMBER_OF_SAMPLE_AUTHORS - 1).getSequence();
        assertThat(ordinals, hasKey(sequenceString));

        Contributor contributor = samplePublication.getEntityDescription().getContributors()
                                                   .get(NUMBER_OF_SAMPLE_AUTHORS - 1);
        assertThat(contributor.getSequence(), is(equalTo(ordinals.get(sequenceString))));
    }

    @Test
    @DisplayName("The earliest year found in the \"published-print\" field is stored in the entity description.")
    public void entityDescriptionContainsTheEarliestYearFoundInPublishedPrintField() {
        String actualYear = samplePublication.getEntityDescription().getDate().getYear();
        assertThat(actualYear, is(equalTo(EXPECTED_YEAR.toString())));
    }

    @Test
    @DisplayName("toPublication sets null EntityDescription date when input has no \"published-print\" date")
    public void entityDescriptionDateIsNullWhenInputDataHasNoPublicationDate() {
        sampleInputDocument.setPublishedPrint(null);
        Publication publicationWithoutDate = toPublication(sampleInputDocument);
        PublicationDate actualDate = publicationWithoutDate.getEntityDescription().getDate();
        assertThat(actualDate, is(nullValue()));
    }

    @Test
    @DisplayName("toPublication sets PublicationType to JournalArticle when the input has the tag \"journal-article\"")
    public void toPublicationSetsPublicationTypeToJournalArticleWhenTheInputHasTheTagJournalArticle() {
        assertThat(samplePublication.getEntityDescription().getPublicationType(),
            is(equalTo(PublicationType.JOURNAL_ARTICLE)));
    }

    @Test
    @DisplayName("toPublication throws Exception when the input does not have the tag \"journal-article\"")
    public void toPublicationSetsThrowsExceptionWhenTheInputDoesNotHaveTheTagJournalArticle() {
        sampleInputDocument.setType(NOT_JOURNAL_ARTICLE);
        IllegalArgumentException exception =
            assertThrows(IllegalArgumentException.class, () -> toPublication(sampleInputDocument));
        assertThat(exception.getMessage(), is(equalTo(CrossRefConverter.NOT_A_JOURNAL_ARTICLE_ERROR)));
    }

    private Publication toPublication(CrossRefDocument doc) {
        return converter.toPublication(doc, NOW, OWNER, DOCID);
    }

    private CrossRefDocument createSampleDocument() {
        CrossRefDocument document = new CrossRefDocument();
        setAuthor(document);
        setPublicationDate(document);
        setTitle(document);
        setPublicationType(document);
        return document;
    }

    private void setPublicationType(CrossRefDocument document) {
        document.setType(CrossRefConverter.JOURNAL_ARTICLE);
    }

    private void setTitle(CrossRefDocument document) {
        List<String> titleArray = Collections.singletonList(SAMPLE_DOCUMENT_TITLE);
        document.setTitle(titleArray);
    }

    private void setPublicationDate(CrossRefDocument document) {
        int[][] dateParts = new int[NUMBER_OF_DATES][DATE_SIZE];
        dateParts[0] = new int[]{EXPECTED_YEAR, 2, 20};
        dateParts[1] = new int[]{UNEXPECTED_YEAR};
        CrossrefDate date = new CrossrefDate();
        date.setDateParts(dateParts);
        document.setPublishedPrint(date);
    }

    private CrossRefDocument setAuthor(CrossRefDocument document) {
        Author author = new Author.Builder()
            .withGivenName(AUTHOR_GIVEN_NAME)
            .withFamilyName(AUTHOR_FAMILY_NAME)
            .withSequence(FIRST_AUTHOR)
            .build();
        document.setAuthor(Collections.singletonList(author));
        return document;
    }
}
