package no.unit.nva.doi.transformer;

import static no.unit.nva.doi.transformer.DataciteResponseConverter.DEFAULT_NEW_PUBLICATION_STATUS;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import no.unit.nva.doi.transformer.model.crossrefmodel.Author;
import no.unit.nva.doi.transformer.model.crossrefmodel.CrossRefDocument;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.EntityDescription;
import no.unit.nva.model.Identity;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationType;

public class CrossRefConverter extends AbstractConverter {

    public static final String NOT_A_JOURNAL_ARTICLE_ERROR = "The entry is not a journal article";
    public static final String MISSING_PUBLICATION_YEAR_ERROR = "Missing publication year";
    private static Map<String, Integer> ordinals;

    private static String JOURNAL_ARTICLE = "journal-article";

    static {
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

    public Publication toPublication(CrossRefDocument document, Instant now, String owner, UUID identifier) {

        return new Publication.Builder()
            .withCreatedDate(now)
            .withModifiedDate(now)
            .withOwner(owner)
            .withIdentifier(identifier)
            .withStatus(DEFAULT_NEW_PUBLICATION_STATUS)
            .withEntityDescription(new EntityDescription.Builder()
                .withContributors(toContributors(document.getAuthor()))
                .withDate(extractDate(document))
                .withMainTitle(extractTitle(document))
                .withPublicationType(extractPublicationType(document))
                .build())
            .build();
    }

    protected PublicationType extractPublicationType(CrossRefDocument document) {
        if (document.getType().toLowerCase().equals(JOURNAL_ARTICLE)) {
            return PublicationType.JOURNAL_ARTICLE;
        } else {
            throw new IllegalArgumentException(NOT_A_JOURNAL_ARTICLE_ERROR);
        }
    }

    private String extractTitle(CrossRefDocument document) {
        return getMainTitle(Arrays.stream(document.getTitle()));
    }

    private PublicationDate extractDate(CrossRefDocument document) {
        Integer earliestYear = crossRefDatesToEarliestPublicationYear(document).orElse(null);
        return toDate(earliestYear);
    }

    protected Optional<Integer> crossRefDatesToEarliestPublicationYear(CrossRefDocument document) {
        if (document.getPublishedPrint() != null) {
            int[][] dateParts = document.getPublishedPrint().getDateParts();
            if (dateParts != null) {
                Optional<Integer> earliestPublicationYear = Arrays.stream(dateParts).filter(this::arrayNotEmpty)
                                                                  .map(x -> x[0])
                                                                  .min(Integer::compareTo);
                return earliestPublicationYear;
            }
        }
        throw new IllegalArgumentException(MISSING_PUBLICATION_YEAR_ERROR);
    }

    private boolean arrayNotEmpty(int[] dateArray) {
        return dateArray.length > 0;
    }

    public List<Contributor> toContributors(List<Author> authors) {
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
        return ordinals.get(sequence.toLowerCase());
    }
}
