package no.unit.nva.doi.transformer;

import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import no.unit.nva.doi.transformer.model.crossrefmodel.Author;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossRefDocument;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossrefDate;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.EntityDescription;
import no.unit.nva.model.Identity;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationType;

public class CrossRefConverter extends AbstractConverter {

    public static final String NOT_A_JOURNAL_ARTICLE_ERROR = "The entry is not a journal article";
    public static final String INVALID_ENTRY_ERROR = "The entry is empty or has no title";
    protected static Map<String, Integer> ordinals;

    // The "journal" publication type in the crossref entries
    public static String JOURNAL_ARTICLE = "journal-article";

    static {
        // The ordinals in crossref entries are defined by "first", "second" etc.
        // We need to map them to numbers.
        ordinals = new HashMap<>();
        ordinals.put("first", 1);
        ordinals.put("second", 2);
        ordinals.put("third", 3);
        ordinals.put("fourth", 4);
        ordinals.put("fifth", 5);
        ordinals.put("sixth", 6);
        ordinals.put("seventh", 7);
        ordinals.put("eighth", 8);
        ordinals.put("ninth", 9);
        ordinals.put("tenth", 10);
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
                                     URI publisherId
    ) {

        if (document != null && hasTitle(document)) {

            return new Publication.Builder()
                .withCreatedDate(now)
                .withModifiedDate(now)
                .withOwner(owner)
                .withIdentifier(identifier)
                .withPublisher(toPublisher(publisherId))
                .withStatus(DEFAULT_NEW_PUBLICATION_STATUS)
                .withEntityDescription(new EntityDescription.Builder()
                    .withContributors(toContributors(document.getAuthor()))
                    .withDate(extractDate(document).orElse(null))
                    .withMainTitle(extractTitle(document))
                    .withPublicationType(extractPublicationType(document))
                    .build())
                .build();
        }
        throw new IllegalArgumentException(INVALID_ENTRY_ERROR);
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

    private Optional<PublicationDate> extractDate(CrossRefDocument document) {
        Optional<Integer> earliestYear = Optional.ofNullable(document.getPublishedPrint())
                                                 .flatMap(CrossrefDate::extractEarliestYear);

        return earliestYear.map(this::toDate);
    }

    protected List<Contributor> toContributors(List<Author> authors) {
        if (authors != null) {
            return authors.stream().map(this::toContributor).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    private Contributor toContributor(Author author) {

        Identity identity = new Identity.Builder()
            .withName(toName(author.getFamilyName(), author.getGivenName()))
            .build();
        return new Contributor.Builder()
            .withIdentity(identity)
            .withSequence(parseSequence(author.getSequence()))
            .build();
    }

    private int parseSequence(String sequence) {
        return ordinals.get(sequence.toLowerCase(Locale.getDefault()));
    }
}
