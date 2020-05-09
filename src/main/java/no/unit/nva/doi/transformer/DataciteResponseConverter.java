package no.unit.nva.doi.transformer;

import no.unit.nva.doi.transformer.language.LanguageDetector;
import no.unit.nva.doi.transformer.language.SimpleLanguageDetector;
import no.unit.nva.doi.transformer.model.internal.external.DataciteContainer;
import no.unit.nva.doi.transformer.model.internal.external.DataciteCreator;
import no.unit.nva.doi.transformer.model.internal.external.DataciteRelatedIdentifier;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteRights;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTitle;
import no.unit.nva.doi.transformer.utils.DataciteRelatedIdentifierType;
import no.unit.nva.doi.transformer.utils.DataciteRelationType;
import no.unit.nva.doi.transformer.utils.DataciteTypesUtil;
import no.unit.nva.doi.transformer.utils.IssnCleaner;
import no.unit.nva.doi.transformer.utils.SingletonCollector;
import no.unit.nva.model.Contributor;
import no.unit.nva.model.EntityDescription;
import no.unit.nva.model.Identity;
import no.unit.nva.model.Journal;
import no.unit.nva.model.Level;
import no.unit.nva.model.NameType;
import no.unit.nva.model.Organization;
import no.unit.nva.model.Publication;
import no.unit.nva.model.PublicationContext;
import no.unit.nva.model.PublicationType;
import no.unit.nva.model.Reference;
import no.unit.nva.model.ResearchProject;
import no.unit.nva.model.exceptions.InvalidIssnException;
import no.unit.nva.model.exceptions.InvalidPageTypeException;
import no.unit.nva.model.exceptions.MalformedContributorException;
import no.unit.nva.model.instancetypes.JournalArticle;
import no.unit.nva.model.instancetypes.PublicationInstance;
import no.unit.nva.model.pages.Range;
import nva.commons.utils.doi.DoiConverter;
import nva.commons.utils.doi.DoiConverterImpl;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.function.Predicate.not;
import static no.unit.nva.model.PublicationType.JOURNAL_CONTENT;

public class DataciteResponseConverter extends AbstractConverter {

    public static final String CREATIVECOMMONS = "creativecommons";
    public static final String INFO_EU_REPO_SEMANTICS_OPEN_ACCESS = "info:eu-repo/semantics/openAccess";

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
     * @throws InvalidPageTypeException when the mapping uses an incorrect page type related to the PublicationContext
     * @throws InvalidIssnException when the ISSN is invalid
     */
    public Publication toPublication(DataciteResponse dataciteResponse, Instant now, UUID identifier, String owner,
                                     URI publisherId) throws URISyntaxException, InvalidPageTypeException,
            InvalidIssnException {

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
                                .withReference(createReference(dataciteResponse))
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

    private Reference createReference(DataciteResponse dataciteResponse) throws InvalidPageTypeException,
            InvalidIssnException {
        return new Reference.Builder()
                .withDoi(createDoi(dataciteResponse.getDoi()))
                .withPublishingContext(extractPublicationContext(dataciteResponse))
                .withPublicationInstance(extractPublicationInstance(dataciteResponse))
                .build();
    }

    private URI createDoi(String doi) {
        DoiConverter doiConverter = new DoiConverterImpl();
        return doiConverter.toUri(doi);
    }

    private PublicationInstance extractPublicationInstance(DataciteResponse dataciteResponse) throws
            InvalidPageTypeException {
        if (getPublicationType(dataciteResponse).equals(JOURNAL_CONTENT)) {

            DataciteContainer container = dataciteResponse.getContainer();
            String issue = Optional.ofNullable(container.getIssue()).orElse(null);
            String volume = Optional.ofNullable(container.getVolume()).orElse(null);
            return new JournalArticle.Builder()
                    .withArticleNumber(null)
                    .withIssue(issue)
                    .withPages(extractPages(container))
                    .withVolume(volume)
                    .withPeerReviewed(true)
                    .build();
        }
        return null;
    }

    private Range extractPages(DataciteContainer container) {
        return new Range.Builder()
                .withBegin(container.getFirstPage())
                .withEnd(container.getLastPage())
                .build();
    }

    private PublicationContext extractPublicationContext(DataciteResponse dataciteResponse) throws
            InvalidIssnException {
        PublicationType type = getPublicationType(dataciteResponse);
        if (nonNull(type) && type.equals(JOURNAL_CONTENT)) {

            return new Journal.Builder()
                    .withPrintIssn(extractPrintIssn(dataciteResponse))
                    .withTitle(dataciteResponse.getContainer().getTitle())
                    .withOnlineIssn(extractOnlineIssn(dataciteResponse))
                    .withPeerReviewed(true)
                    .withOpenAccess(extractOpenAccess(dataciteResponse))
                    .withLevel(Level.NO_LEVEL)
                    .build();
        }
        return null;
    }

    private String extractOnlineIssn(DataciteResponse dataciteResponse) {
        DataciteRelatedIdentifier result = getPartOfRelations(dataciteResponse)
                .stream()
                .filter(this::isOnlineIssn)
                .collect(SingletonCollector.collectOrElse(null));
        return nonNull(result) ? IssnCleaner.clean(result.getRelatedIdentifier()) : null;

    }

    private boolean isOnlineIssn(DataciteRelatedIdentifier identifier) {
        return DataciteRelatedIdentifierType.getByCode(identifier.getRelatedIdentifierType())
                .equals(DataciteRelatedIdentifierType.EISSN);
    }

    private String extractPrintIssn(DataciteResponse dataciteResponse) {
        DataciteRelatedIdentifier result = getPartOfRelations(dataciteResponse)
                .stream()
                .filter(this::isPrintIssn)
                .collect(SingletonCollector.collectOrElse(null));
        return nonNull(result) ? IssnCleaner.clean(result.getRelatedIdentifier()) : null;
    }

    private List<DataciteRelatedIdentifier> getPartOfRelations(DataciteResponse dataciteResponse) {
        return dataciteResponse.getRelatedIdentifiers()
                .stream()
                .filter(this::isPartOf)
                .collect(Collectors.toList());
    }

    private boolean isPrintIssn(DataciteRelatedIdentifier identifier) {
        return DataciteRelatedIdentifierType.getByCode(identifier.getRelatedIdentifierType())
                .equals(DataciteRelatedIdentifierType.ISSN);
    }

    private boolean isPartOf(DataciteRelatedIdentifier identifer) {
        return DataciteRelationType.getByRelation(identifer.getRelationType())
                .equals(DataciteRelationType.IS_PART_OF);
    }

    private boolean extractOpenAccess(DataciteResponse dataciteResponse) {
        List<DataciteRights> rights = Optional.ofNullable(dataciteResponse.getRightsList()).orElse(null);
        if (nonNull(rights) && !rights.isEmpty()) {
            return rights.stream().anyMatch(this::hasOpenAccessRights);
        }
        return false;
    }

    private boolean hasOpenAccessRights(DataciteRights dataciteRights) {
        AtomicBoolean result = new AtomicBoolean(false);
        Optional.ofNullable(dataciteRights.getRightsUri()).ifPresentOrElse(
            (r) -> result.set(r.contains(CREATIVECOMMONS)
                || r.equals(INFO_EU_REPO_SEMANTICS_OPEN_ACCESS)),
            () -> result.set(false));
        return result.get();
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
