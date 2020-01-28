package no.unit.nva.doi.transformer;

import no.unit.nva.doi.transformer.model.internal.external.DataciteCreator;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTitle;
import no.unit.nva.doi.transformer.model.internal.internal.Creator;
import no.unit.nva.doi.transformer.model.internal.internal.Metadata;
import no.unit.nva.doi.transformer.model.internal.internal.Resource;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.Instant.now;

public class DataciteResponseConverter {

    public static final String DEFAULT_NEW_RESOURCE_STATUS = "DRAFT";

    /**
     * Convert Datacite response data to internal NVA data.
     *
     * @param dataciteResponse  dataciteResponse
     * @param resourceIdentifier resourceIdentifier
     * @param owner owner
     * @return  resource
     */
    public Resource toResource(DataciteResponse dataciteResponse, String resourceIdentifier, String owner) {

        Instant now = now();

        return new Resource.Builder()
                .withCreatedDate(now.toString())
                .withModifiedDate(now.toString())
                .withOwner(owner)
                .withResourceIdentifier(resourceIdentifier)
                .withStatus(DEFAULT_NEW_RESOURCE_STATUS)
                .withMetadata(new Metadata.Builder()
                        .withCreators(toCreators(dataciteResponse.getCreators()))
                        .withPublicationYear(dataciteResponse.getPublicationYear().toString())
                        .withTitles(toTitles(dataciteResponse.getTitles()))
                        .withResourceType(dataciteResponse.getTypes().getResourceType())
                        .build())
                .withFiles(Collections.emptyMap())
                .build();
    }

    private Map<String, String> toTitles(List<DataciteTitle> titles) {
        return titles
                .stream()
                .collect(Collectors.toConcurrentMap(dataciteTitle -> {
                    return "";
                }, DataciteTitle::getTitle));
    }

    private List<Creator> toCreators(List<DataciteCreator> creators) {
        return creators
                .stream()
                .map(this::toCreator)
                .collect(Collectors.toList());
    }

    private Creator toCreator(DataciteCreator dataciteCreator) {
        return new Creator.Builder()
                .withIdentifier(dataciteCreator.getName())
                .build();
    }

}
