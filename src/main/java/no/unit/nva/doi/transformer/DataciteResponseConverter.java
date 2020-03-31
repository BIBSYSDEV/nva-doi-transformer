package no.unit.nva.doi.transformer;

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
import no.unit.nva.doi.transformer.language.LanguageDetector;
import no.unit.nva.doi.transformer.language.SimpleLanguageDetector;
import no.unit.nva.doi.transformer.model.internal.external.DataciteCreator;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTitle;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.EntityDescription;
import no.unit.nva.model.Identity;
import no.unit.nva.model.License;
import no.unit.nva.model.NameType;
import no.unit.nva.model.Organization;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationSubtype;
import no.unit.nva.model.PublicationType;
import no.unit.nva.model.Reference;
import no.unit.nva.model.ResearchProject;

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
            .withLicense(extractLicence())
            .withProject(extractProject())
            .withEntityDescription(
                new EntityDescription.Builder()
                    .withContributors(toContributors(dataciteResponse.getCreators()))
                    .withDate(toDate(dataciteResponse.getPublicationYear()))
                    .withMainTitle(extractMainTitle(dataciteResponse))
                    .withAbstract(extractAbstract())
                    .withAlternativeTitles(extractAlternativeTitles(dataciteResponse))
                    .withPublicationType(extractPublicationType(dataciteResponse))
                    .withPublicationSubtype(createPublicationSubType())
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
                               .filter(t -> !t.getTitle().equals(mainTitle))
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

    private Reference createReference() {
        return null;
    }

    private String extractAbstract() {
        return null;
    }

    private URI createLanguage() {
        return null;
    }

    private PublicationSubtype createPublicationSubType() {
        return null;
    }

    private PublicationType extractPublicationType(DataciteResponse dataciteResponse) {
        return PublicationType.lookup(dataciteResponse.getTypes().getResourceType());
    }

    private Instant extractPublishedDate() {
        return null;
    }

    private ResearchProject extractProject() {
        return null;
    }

    private License extractLicence() {
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
        return new Contributor.Builder().withIdentity(
            new Identity.Builder().withName(toName(dataciteCreator)).withNameType(
                NameType.lookup(dataciteCreator.getNameType())).build()).withAffiliations(
            toAffilitations()).withSequence(sequence).build();
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
