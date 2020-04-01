package no.unit.nva.doi.transformer;

import static java.util.function.Predicate.not;

import com.ibm.icu.text.RuleBasedNumberFormat;
import java.net.URI;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import no.unit.nva.doi.transformer.language.LanguageMapper;
import no.unit.nva.doi.transformer.language.SimpleLanguageDetector;
import no.unit.nva.doi.transformer.model.crossrefmodel.Author;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossRefDocument;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossrefDate;
import no.unit.nva.doi.transformer.utils.StringUtils;
import no.unit.nva.doi.transformer.utils.TextLang;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.EntityDescription;
import no.unit.nva.model.FileSet;
import no.unit.nva.model.Identity;
import no.unit.nva.model.License;
import no.unit.nva.model.Pages;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationContext;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationInstance;
import no.unit.nva.model.PublicationSubtype;
import no.unit.nva.model.PublicationType;
import no.unit.nva.model.Reference;
import no.unit.nva.model.ResearchProject;

public class CrossRefConverter extends AbstractConverter {

    public static final String NOT_A_JOURNAL_ARTICLE_ERROR = "The entry is not a journal article";
    public static final String INVALID_ENTRY_ERROR = "The entry is empty or has no title";
    public static final URI CROSSEF_URI = URI.create("https://www.crossref.org/");
    public static final String CROSSREF = "crossref";
    // The "journal" publication type in the crossref entries
    public static String JOURNAL_ARTICLE = "journal-article";

    public CrossRefConverter() {
        super(new SimpleLanguageDetector());
    }

    /**
     * Creates a publication.
     *
     * @param document   a Java representation of a CrossRef document.
     * @param now        Instant.
     * @param owner      the owning institution.
     * @param identifier the publication identifier.
     * @return a internal representation of the publication.
     */
    public Publication toPublication(CrossRefDocument document,
                                     Instant now,
                                     String owner,
                                     UUID identifier,
                                     URI publisherId) {

        if (document != null && hasTitle(document)) {

            return new Publication.Builder()
                .withCreatedDate(now)
                .withModifiedDate(now)
                .withPublishedDate(createPublishedDate())
                .withOwner(owner)
                .withIdentifier(identifier)
                .withPublisher(toPublisher(publisherId))
                .withStatus(DEFAULT_NEW_PUBLICATION_STATUS)
                .withIndexedDate(createIndexedDate())
                .withHandle(createHandle())
                .withLink(createLink())
                .withProject(createProject())
                .withLicense(createLicense())
                .withFileSet(createFilseSet())
                .withEntityDescription(new EntityDescription.Builder()
                    .withContributors(toContributors(document.getAuthor()))
                    .withDate(extractDate(document).orElse(null))
                    .withMainTitle(extractTitle(document))
                    .withAlternativeTitles(extractAlternativeTitles(document))
                    .withPublicationType(extractPublicationType(document))
                    .withPublicationSubtype(extractPublicationSubtype())
                    .withAbstract(extractAbstract(document))
                    .withLanguage(extractLanguage(document))
                    .withNpiSubjectHeading(extractNpiSubjectHeading())
                    .withTags(extractTags())
                    .withDescription(extractDescription())
                    .withReference(extractReference(document))
                    .withMetadataSource(extractMetadataSource(document))
                    .build())
                .build();
        }
        throw new IllegalArgumentException(INVALID_ENTRY_ERROR);
    }

    private URI extractMetadataSource(CrossRefDocument document) {
        if (containsCrossrefAsSource(document)) {
            return CROSSEF_URI;
        } else {
            return tryCreatingUri(document.getSource()).orElse(null);
        }
    }

    private boolean containsCrossrefAsSource(CrossRefDocument document) {
        return Optional.ofNullable(document.getSource())
                       .map(str -> str.toLowerCase(Locale.getDefault()))
                       .filter(str -> str.contains(CROSSREF))
                       .isPresent();
    }

    private Optional<URI> tryCreatingUri(String source) {
        try {
            return Optional.of(URI.create(source));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private Reference extractReference(CrossRefDocument document) {
        PublicationContext context = new PublicationContext.Builder()
            .withLevel(null)
            .withName(extractJournalTitle(document))
            .build();
        PublicationInstance instance = new PublicationInstance.Builder()
            .withVolume(document.getVolume())
            .withIssue(document.getIssue())
            .withPages(extractPages(document))
            .build();
        return new Reference.Builder()
            .withDoi(document.getDoi())
            .withPublishingContext(context)
            .withPublicationInstance(instance)
            .build();
    }

    private Pages extractPages(CrossRefDocument document) {
        return StringUtils.parsePage(document.getPage());
    }

    private String extractJournalTitle(CrossRefDocument document) {
        return Optional.ofNullable(document.getContainerTitle())
                .stream()
                .flatMap(Collection::stream)
                .findFirst()
                .orElse(null);

    }

    private String extractDescription() {
        return null;
    }

    private URI extractLanguage(CrossRefDocument document) {
        return LanguageMapper.getUriOpt(document.getLanguage()).orElse(null);
    }

    private String extractAbstract(CrossRefDocument document) {
        return Optional.ofNullable(document.getAbstractText())
                       .map(StringUtils::removeXmlTags)
                       .orElse(null);
    }

    private Map<String, String> extractAlternativeTitles(CrossRefDocument document) {
        String mainTitle = extractTitle(document);
        return document.getTitle().stream()
                       .filter(not(title -> title.equals(mainTitle)))
                       .map(this::detectLanguage)
                       .collect(Collectors.toConcurrentMap(TextLang::getText, e -> e.getLanguage().toString()));
    }

    private PublicationSubtype extractPublicationSubtype() {
        return null;
    }

    private boolean hasTitle(CrossRefDocument document) {
        return document.getTitle() != null && !document.getTitle().isEmpty();
    }

    protected PublicationType extractPublicationType(CrossRefDocument document) {
        if (document.getType().equalsIgnoreCase(JOURNAL_ARTICLE)) {
            return PublicationType.JOURNAL_ARTICLE;
        } else {
            throw new IllegalArgumentException(NOT_A_JOURNAL_ARTICLE_ERROR);
        }
    }

    private String extractTitle(CrossRefDocument document) {
        return getMainTitle(document.getTitle().stream());
    }

    /**
     * For more details about how date is extracted see {@link CrossrefDate}.
     *
     * @param document A crossref JSON document
     * @return The earliest year found in publication dates
     */
    private Optional<PublicationDate> extractDate(CrossRefDocument document) {
        Optional<Integer> earliestYear = Optional.ofNullable(document.getPublishedPrint())
                                                 .flatMap(CrossrefDate::extractEarliestYear);

        return earliestYear.map(this::toDate);
    }

    protected List<Contributor> toContributors(List<Author> authors) {
        if (authors != null) {
            return IntStream.range(0, authors.size()).mapToObj(i -> toContributor(authors.get(i), i + 1))
                            .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private Contributor toContributor(Author author, int alternativeSequence) {

        Identity identity = new Identity.Builder().withName(toName(author.getFamilyName(), author.getGivenName()))
                                                  .build();
        return new Contributor.Builder().withIdentity(identity)
                                        .withSequence(parseSequence(author.getSequence(), alternativeSequence)).build();
    }

    /**
     * Parses the "sequence" field of the cross-ref document, The "sequence" field shows if the author is the 1st, 2nd,
     * etc. author
     *
     * @param sequence ordinal string e.g. "first"
     * @return Ordinal in number format. "first" -> 1, "second" -> 2, etc.
     */
    private int parseSequence(String sequence, int alternativeSequence) {
        RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.UK, RuleBasedNumberFormat.SPELLOUT);
        try {
            return nf.parse(sequence).intValue();
        } catch (Exception e) {
            return alternativeSequence;
        }
    }

    private List<String> extractTags() {
        return null;
    }

    private String extractNpiSubjectHeading() {
        return null;
    }

    private FileSet createFilseSet() {
        return null;
    }

    private License createLicense() {
        return null;
    }

    private ResearchProject createProject() {
        return null;
    }

    private URI createLink() {
        return null;
    }

    private URI createHandle() {
        return null;
    }

    private Instant createIndexedDate() {
        return null;
    }

    private Instant createPublishedDate() {
        return null;
    }
}
