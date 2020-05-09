package no.unit.nva.doi.transformer;

import no.unit.nva.doi.transformer.language.LanguageDetector;
import no.unit.nva.doi.transformer.language.SimpleLanguageDetector;
import no.unit.nva.doi.transformer.model.internal.external.DataciteCreator;
import no.unit.nva.doi.transformer.model.internal.external.DataciteIdentifier;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTitle;
import no.unit.nva.doi.transformer.utils.DataciteTypesUtil;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.EntityDescription;
import no.unit.nva.model.Identity;
import no.unit.nva.model.Journal;
import no.unit.nva.model.NameType;
import no.unit.nva.model.Organization;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationContext;
import no.unit.nva.model.PublicationType;
import no.unit.nva.model.Reference;
import no.unit.nva.model.ResearchProject;
import no.unit.nva.model.exceptions.InvalidIssnException;
import no.unit.nva.model.exceptions.MalformedContributorException;
import no.unit.nva.model.instancetypes.JournalArticle;
import no.unit.nva.model.instancetypes.PublicationInstance;
import nva.commons.utils.doi.DoiConverter;
import nva.commons.utils.doi.DoiConverterImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;
import static no.unit.nva.model.PublicationType.JOURNAL_CONTENT;

public class DataciteResponseConverter extends AbstractConverter {

    public DataciteResponseConverter() {
        this(new SimpleLanguageDetector());
    }

    public DataciteResponseConverter(LanguageDetector languageDetector) {
        super(languageDetector);
    }

    /**
     * Convert Datacite response data to NVA Publication.
     *
     * @param dataciteResponse dataciteResponse
     * @param identifier       identifier
     * @param owner            owner
     * @return publication
     * @throws URISyntaxException when dataciteResponse contains invalid URIs
     */
    public Publication toPublication(DataciteResponse dataciteResponse, Instant now, UUID identifier, String owner,
                                     URI publisherId) throws URISyntaxException {

        return new Publication.Builder()
            .withCreatedDate(now)
            .withModifiedDate(now)
            .withPublishedDate(extractPublishedDate())
            .withOwner(owner)
            .withPublisher(toPublisher(publisherId))
            .withIdentifier(identifier)
            .withStatus(DEFAULT_NEW_PUBLICATION_STATUS)
            .withHandle(extractHandle())
            .withLink(extractLink(dataciteResponse))
            .withIndexedDate(extractIndexedDate())
            .withProject(extractProject())
            .withEntityDescription(
                new EntityDescription.Builder()
                    .withContributors(toContributors(dataciteResponse.getCreators()))
                    .withDate(toDate(dataciteResponse.getPublicationYear()))
                    .withMainTitle(extractMainTitle(dataciteResponse))
                    .withAbstract(extractAbstract())
                    .withAlternativeTitles(extractAlternativeTitles(dataciteResponse))
                    .withLanguage(createLanguage())
                    .withReference(createReference())
                    .withTags(createTags())
                    .withDescription(createDescription())
                    .build())
            .build();
    }

    private Map<String, String> extractAlternativeTitles(DataciteResponse dataciteResponse) {
        String mainTitle = extractMainTitle(dataciteResponse);
        return dataciteResponse.getTitles().stream()
            .filter(not(t -> t.getTitle().equals(mainTitle)))
            .map(t -> detectLanguage(t.getTitle()))
            .map(e -> new SimpleEntry<>(e.getText(), e.getLanguage().toString()))
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
    }

    private String createDescription() {
        return null;
    }

    private List<String> createTags() {
        return null;
    }

    private Reference createReference(DataciteResponse dataciteResponse) {
        return new Reference.Builder()
                .withDoi(doiConverter.toUri(dataciteResponse.getDoi()))
                .withPublishingContext(extractPublicationContext(dataciteResponse))
                .withPublicationInstance(extractPublicationInstance(dataciteResponse))
                .build();
    }

    private PublicationInstance extractPublicationInstance(DataciteResponse dataciteResponse) {
        if (getPublicationType(dataciteResponse).equals(JOURNAL_CONTENT)) {
            List<DataciteIdentifier> identifiers = dataciteResponse.getIdentifiers();


/*
            return new JournalArticle.Builder()
                    .withArticleNumber(null)
                    .withIssue(null)
                    .withPages(null)
                    .withVolume(null)
                    .withPeerReviewed(true)
                    .build();

 */
            return null;
        }
        return null;
    }

    private PublicationContext extractPublicationContext(DataciteResponse dataciteResponse) {
        PublicationType type = getPublicationType(dataciteResponse);
        if (nonNull(type) && type.equals(JOURNAL_CONTENT)) {
            try {
                return new Journal.Builder()
                        .build();
            } catch (InvalidIssnException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private PublicationType getPublicationType(DataciteResponse dataciteResponse) {
        return DataciteTypesUtil.mapToType(dataciteResponse);
    }

    private String extractAbstract() {
        return null;
    }

    private URI createLanguage() {
        return null;
    }

    private Instant extractPublishedDate() {
        return null;
    }

    private ResearchProject extractProject() {
        return null;
    }

    private Instant extractIndexedDate() {
        return null;
    }

    private URI extractLink(DataciteResponse dataciteResponse) throws URISyntaxException {
        return dataciteResponse.getUrl().toURI();
    }

    private URI extractHandle() {
        return null;
    }

    protected String extractMainTitle(DataciteResponse response) {
        Stream<String> titleStrings = response.getTitles().stream().map(DataciteTitle::getTitle);
        return getMainTitle(titleStrings);
    }

    protected List<Contributor> toContributors(List<DataciteCreator> creators) {
        return IntStream.range(0, creators.size()).mapToObj(i -> toCreator(creators.get(i), i + 1)).collect(
                Collectors.toList());
    }

    protected Contributor toCreator(DataciteCreator dataciteCreator, Integer sequence) {
        try {
            return new Contributor.Builder().withIdentity(
                    new Identity.Builder().withName(toName(dataciteCreator)).withNameType(
                            NameType.lookup(dataciteCreator.getNameType())).build()).withAffiliations(
                    toAffilitations()).withSequence(sequence).build();
        } catch (MalformedContributorException e) {
            return null;
        }
    }

    protected List<Organization> toAffilitations() {
        return null;
    }

    protected String toName(DataciteCreator dataciteCreator) {
        if (dataciteCreator.getName() != null) {
            return dataciteCreator.getName();
        } else {
            return super.toName(dataciteCreator.getFamilyName(), dataciteCreator.getGivenName());
        }
    }
}
