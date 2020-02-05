package no.unit.nva.doi.transformer;

import no.unit.nva.doi.transformer.model.internal.external.DataciteAffiliation;
import no.unit.nva.doi.transformer.model.internal.external.DataciteCreator;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTitle;
import no.unit.nva.doi.transformer.model.internal.internal.Contributor;
import no.unit.nva.doi.transformer.model.internal.internal.EntityDescription;
import no.unit.nva.doi.transformer.model.internal.internal.EntityType;
import no.unit.nva.doi.transformer.model.internal.internal.NameType;
import no.unit.nva.doi.transformer.model.internal.internal.Publication;
import no.unit.nva.doi.transformer.model.internal.internal.PublicationDate;
import no.unit.nva.doi.transformer.model.internal.internal.PublicationStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.time.Instant.now;

public class DataciteResponseConverter {

    public static final PublicationStatus DEFAULT_NEW_PUBLICATION_STATUS = PublicationStatus.NEW;

    /**
     * Convert Datacite response data to NVA Publication.
     *
     * @param dataciteResponse  dataciteResponse
     * @param identifier identifier
     * @param owner owner
     * @return  publication
     */
    public Publication toPublication(DataciteResponse dataciteResponse, UUID identifier, String owner) {

        Instant now = now();

        return new Publication.Builder()
                .withCreatedDate(now)
                .withModifiedDate(now)
                .withOwner(owner)
                .withIdentifier(identifier)
                .withStatus(DEFAULT_NEW_PUBLICATION_STATUS)
                .withEntityDescription(new EntityDescription.Builder()
                        .withContributors(toContributors(dataciteResponse.getCreators()))
                        .withDate(toDate(dataciteResponse.getPublicationYear()))
                        .withMainTitle(getMainTitle(dataciteResponse.getTitles()))
                        .withType(EntityType.lookup(dataciteResponse.getTypes().getResourceType()))
                        .build())
                .build();
    }

    private String getMainTitle(List<DataciteTitle> titles) {
        return titles.stream().map(DataciteTitle::getTitle).findFirst().orElse("");
    }

    protected PublicationDate toDate(Integer publicationYear) {
        return new PublicationDate.Builder()
                .withYear(publicationYear.toString())
                .build();
    }

    protected List<Contributor> toContributors(List<DataciteCreator> creators) {
        AtomicInteger counter = new AtomicInteger();
        return creators
                .stream()
                .map(dataciteCreator -> toCreator(dataciteCreator, counter.getAndIncrement()))
                .collect(Collectors.toList());
    }

    protected Contributor toCreator(DataciteCreator dataciteCreator, Integer sequence) {
        return new Contributor.Builder()
                .withName(toName(dataciteCreator))
                .withNameType(NameType.lookup(dataciteCreator.getNameType()))
                .withAffiliation(toAffilitation(dataciteCreator.getAffiliation()))
                .withSequence(sequence)
                .build();
    }

    protected List<String> toAffilitation(List<DataciteAffiliation> affiliation) {
        return null;
    }

    protected String toName(DataciteCreator dataciteCreator) {
        if (dataciteCreator.getName() != null) {
            return dataciteCreator.getName();
        } else {
            return String.join(", ", dataciteCreator.getFamilyName(), dataciteCreator.getGivenName());
        }
    }

}
