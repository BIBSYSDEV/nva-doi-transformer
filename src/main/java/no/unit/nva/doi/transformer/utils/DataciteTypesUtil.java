package no.unit.nva.doi.transformer.utils;

import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTypes;
import no.unit.nva.model.PublicationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public class DataciteTypesUtil {

    public static final String TYPE_TEXT = "text";
    public static final String JOURNAL = "journal";
    public static final String ARTICLE = "article";
    public static final int SINGLETON = 1;
    public static final int ONLY_ELEMENT = 0;

    /**
     * Maps the potentially many content types found in a Datacite response to a PublicationType.
     *
     * @param dataciteResponse a DataciteResponse document.
     * @return a PublicationType.
     */
    public static PublicationType mapToType(DataciteResponse dataciteResponse) {
        DataciteTypes types = dataciteResponse.getTypes();
        String resourceType = Optional.ofNullable(types.getResourceTypeGeneral()).orElse(null);
        if (nonNull(resourceType) && resourceType.equalsIgnoreCase(TYPE_TEXT)) {
            return getAnalyzedType(types);
        }
        return null;
    }

    private static PublicationType getAnalyzedType(DataciteTypes types) {
        List<PublicationType> publicationTypeList = new ArrayList<>();

        Optional.ofNullable(types.getBibtex().getPublicationType()).ifPresent(publicationTypeList::add);
        Optional.ofNullable(types.getCiteproc().getPublicationType()).ifPresent(publicationTypeList::add);
        Optional.ofNullable(types.getRis().getPublicationType()).ifPresent(publicationTypeList::add);
        Optional.ofNullable(types.getSchemaOrg().getPublicationType()).ifPresent(publicationTypeList::add);

        if (dataCiteResourceContainsJournalArticle(types.getResourceType())) {
            publicationTypeList.add(PublicationType.JOURNAL_CONTENT);
        }

        if (publicationTypeList.isEmpty()) {
            return null;
        }

        if (publicationTypeList.size() == SINGLETON) {
            return publicationTypeList.get(ONLY_ELEMENT);
        }

        Map<PublicationType, Long> typeCounts = getTypeCounts(publicationTypeList);
        return typeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .filter(m -> m.getValue() > 1)
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static Map<PublicationType, Long> getTypeCounts(List<PublicationType> list) {
        return list.stream().collect(Collectors.groupingBy(type -> type, Collectors.counting()));
    }

    private static boolean dataCiteResourceContainsJournalArticle(String resourceType) {
        String uncontrolledResourceType = resourceType.toLowerCase(Locale.ENGLISH);
        return uncontrolledResourceType.contains(JOURNAL) && uncontrolledResourceType.contains(ARTICLE);
    }
}