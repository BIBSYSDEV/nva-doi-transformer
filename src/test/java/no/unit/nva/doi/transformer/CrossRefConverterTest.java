package no.unit.nva.doi.transformer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.text.IsEmptyString.emptyString;
import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import no.bibsys.aws.tools.IoUtils;
import no.unit.nva.doi.transformer.language.LanguageMapper;
import no.unit.nva.doi.transformer.model.crossrefmodel.Author;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossRefDocument;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossrefApiResponse;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossrefDate;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CrossRefConverterTest extends ConversionTest {

    private static final Instant NOW = Instant.now();
    public static final String AUTHOR_GIVEN_NAME = "givenName";
    public static final String AUTHOR_FAMILY_NAME = "familyName";
    public static final String FIRST_AUTHOR = "first";
    private static final int DATE_SIZE = 3;
    private static final int NUMBER_OF_DATES = 2;
    public static final Integer EXPECTED_YEAR = 2019;
    public static final int UNEXPECTED_YEAR = EXPECTED_YEAR + 1;
    private static final String SAMPLE_DOCUMENT_TITLE = "Sample document title";
    private static final Integer NUMBER_OF_SAMPLE_AUTHORS = 2;
    public static final String SURNAME_COMMA_FIRSTNAME = "%s,.*%s";
    public static final String NOT_JOURNAL_ARTICLE = "book";
    private static final UUID DOC_ID = UUID.randomUUID();
    private static final String OWNER = "TheOwner";
    private static final String INVALID_ORDINAL = "invalid ordinal";
    public static final String SECOND_AUTHOR = "second";
    public static final String CROSSREF_WITH_ABSTRACT_JSON = "crossrefWithAbstract.json";
    private static final String PROCESSED_ABSTRACT = "processedAbstract.txt";
    public static final String ENG_ISO_639_3 = "eng";

    private CrossRefDocument sampleInputDocument = createSampleDocument();
    private final CrossRefConverter converter = new CrossRefConverter();
    private Publication samplePublication;
    private static ObjectMapper objectMapper = MainHandler.createObjectMapper();

    @BeforeEach
    public void init() {
        sampleInputDocument = createSampleDocument();
        samplePublication = toPublication(sampleInputDocument);
    }

    @Test
    @DisplayName("An empty CrossRef document throws IllegalArgument exception")
    public void anEmptyCrossRefDocumentThrowsIllegalArgumentException() throws IllegalArgumentException {
        CrossRefDocument doc = new CrossRefDocument();
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> toPublication(doc));
        assertThat(exception.getMessage(), is(CrossRefConverter.INVALID_ENTRY_ERROR));
    }

    @Test
    @DisplayName("The creator's name in the publication contains first family and then given name")
    public void creatorsNameContainsFirstFamilyAndThenGivenName() throws IllegalArgumentException {

        List<Contributor> contributors = samplePublication.getEntityDescription().getContributors();

        assertThat(contributors.size(), is(equalTo(sampleInputDocument.getAuthor().size())));
        assertThat(contributors.size(), is(equalTo(NUMBER_OF_SAMPLE_AUTHORS)));

        String actualName = contributors.get(NUMBER_OF_SAMPLE_AUTHORS - 1).getIdentity().getName();
        String givenName = sampleInputDocument.getAuthor().get(NUMBER_OF_SAMPLE_AUTHORS - 1).getGivenName();
        assertThat(actualName, containsString(givenName));

        String familyName = sampleInputDocument.getAuthor().get(NUMBER_OF_SAMPLE_AUTHORS - 1).getFamilyName();
        assertThat(actualName, containsString(familyName));

        String expectedNameRegEx = String.format(SURNAME_COMMA_FIRSTNAME, familyName, givenName);
        assertThat(actualName, matchesPattern(expectedNameRegEx));
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> toPublication(sampleInputDocument));
        assertThat(exception.getMessage(), is(equalTo(CrossRefConverter.NOT_A_JOURNAL_ARTICLE_ERROR)));
    }

    @Test
    @DisplayName("toPublication sets as sequence the position of the author in the list when ordinal is not numerical")
    public void toPublicationSetsOrdinalAsSecondAuthorIfInputOrdinalIsNotAValidOrdinal() {
        int numberOfAuthors = sampleInputDocument.getAuthor().size();
        sampleInputDocument.getAuthor().forEach(a -> {
            a.setSequence(INVALID_ORDINAL);
        });
        Publication publication = toPublication(sampleInputDocument);
        List<Integer> ordinals = publication.getEntityDescription().getContributors().stream()
                                            .map(Contributor::getSequence).collect(Collectors.toList());
        assertThat(ordinals.size(), is(numberOfAuthors));
        List<Integer> expectedValues = IntStream.range(0, numberOfAuthors).map(this::startCountingFromOne).boxed()
                                                .collect(Collectors.toList());
        assertThat(ordinals, contains(expectedValues.toArray()));
    }

    private int startCountingFromOne(int i) {
        return i + 1;
    }

    @Test
    @DisplayName("toPublication sets the correct number when the sequence ordinal is valid")
    public void toPublicationSetsCorrectNumberForValidOrdinal() {
        Author author = sampleInputDocument.getAuthor().stream().findFirst().get();
        String validOrdinal = "second";
        int expected = 2;
        author.setSequence(validOrdinal);

        int actual = toPublication(sampleInputDocument).getEntityDescription().getContributors().stream().findFirst()
                                                       .get().getSequence();
        assertThat(actual, is(equalTo(expected)));
    }

    @Test
    @DisplayName("toPublication sets abstract when input has non empty abstract")
    public void toPublicationSetsAbstractWhenInputHasNonEmptyAbstract() throws IOException {
        String json = IoUtils.resourceAsString(Path.of(CROSSREF_WITH_ABSTRACT_JSON));
        CrossRefDocument docWithAbstract = objectMapper.readValue(json, CrossrefApiResponse.class).getMessage();
        String abstractText = toPublication(docWithAbstract).getEntityDescription().getAbstract();
        assertThat(abstractText, is(not(emptyString())));
        String expectedAbstract = IoUtils.resourceAsString(Path.of(PROCESSED_ABSTRACT));
        assertThat(abstractText, is(equalTo(expectedAbstract)));
    }

    @Test
    @DisplayName("toPublication sets the language to a URI when the input is an ISO639-3 entry")
    public void toPublicationSetsTheLanguageToAUriWhenTheInputFollowsTheIso3Standard() {
        Locale sampleLanguage = Locale.ENGLISH;
        sampleInputDocument.setLanguage(sampleLanguage.getISO3Language());
        URI actualLanguage = toPublication(sampleInputDocument).getEntityDescription().getLanguage();
        URI expectedLanguage = LanguageMapper.getURI(ENG_ISO_639_3);
        assertThat(actualLanguage, is(equalTo(expectedLanguage)));
        assertThat(actualLanguage, is(notNullValue()));
    }

    private Publication toPublication(CrossRefDocument doc) {
        return converter.toPublication(doc, NOW, OWNER, DOC_ID, SOME_PUBLISHER_URI);
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
        Author author = new Author.Builder().withGivenName(AUTHOR_GIVEN_NAME).withFamilyName(AUTHOR_FAMILY_NAME)
                                            .withSequence(FIRST_AUTHOR).build();
        Author secondAuthor = new Author.Builder().withGivenName(AUTHOR_GIVEN_NAME).withFamilyName(AUTHOR_FAMILY_NAME)
                                                  .withSequence(SECOND_AUTHOR).build();
        List<Author> authors = Arrays.asList(author, secondAuthor);
        document.setAuthor(authors);
        return document;
    }
}
