package no.unit.nva.doi.transformer.utils;

import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTypes;
import nva.commons.utils.SingletonCollector;

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
    public static final int SINGLE_ELEMENT = 1;

    /**
     * Maps the potentially many content types found in a Datacite response to a PublicationType.
     *
     * @param dataciteResponse a DataciteResponse document.
     * @return a PublicationType.
     */
    public static PublicationType mapToType(DataciteResponse dataciteResponse) {
        DataciteTypes types = dataciteResponse.getTypes();
        return Optional.ofNullable(types)
                .filter(DataciteTypesUtil::isTextType)
                .map(DataciteTypesUtil::getAnalyzedType)
                .orElse(null);
    }

    private static boolean isTextType(DataciteTypes dataciteTypes) {
        return nonNull(dataciteTypes.getResourceTypeGeneral())
                && dataciteTypes.getResourceTypeGeneral().equalsIgnoreCase(TYPE_TEXT);
    }

    private static PublicationType getAnalyzedType(DataciteTypes types) {
        List<PublicationType> publicationTypeList = populateTypeList(types);

        if (publicationTypeList.isEmpty()) {
            return null;
        }

        if (publicationTypeList.size() == SINGLETON) {
            return publicationTypeList.get(ONLY_ELEMENT);
        }

        Map<PublicationType, Long> publicationTypeTally = generatePublicationTypeOccurenceTally(publicationTypeList);
        return findMostAppliedMapping(publicationTypeTally);
    }

    /**
     * This method takes the mapped valued and sorts them by occurrence in case there is disagreement regarding how
     * the provided types should be mapped. If there is only one mapping, this is returned. If there are multiple
     * mappings but no agreement between two-or-more of these, then null is returned.
     *
     * @param publicationTypeTally a map of types and number of occurrences.
     * @return the most mapped type.
     */
    private static PublicationType findMostAppliedMapping(Map<PublicationType, Long> publicationTypeTally) {
        if (publicationTypeTally.size() == SINGLE_ELEMENT) {
            return publicationTypeTally
                    .keySet()
                    .stream()
                    .collect(SingletonCollector.collect());
        }

        return publicationTypeTally.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .filter(tallyMap -> tallyMap.getValue() > 1)
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private static List<PublicationType> populateTypeList(DataciteTypes types) {
        List<PublicationType> publicationTypeList = new ArrayList<>();

        Optional.ofNullable(types.getBibtex())
                .map(BibTexType::getPublicationType)
                .ifPresent(publicationTypeList::add);
        Optional.ofNullable(types.getCiteproc())
                .map(CiteProcType::getPublicationType)
                .ifPresent(publicationTypeList::add);
        Optional.ofNullable(types.getRis())
                .map(RisType::getPublicationType)
                .ifPresent(publicationTypeList::add);
        Optional.ofNullable(types.getSchemaOrg())
                .map(SchemaOrgType::getPublicationType)
                .ifPresent(publicationTypeList::add);

        if (dataCiteResourceContainsJournalArticle(types.getResourceType())) {
            publicationTypeList.add(PublicationType.JOURNAL_CONTENT);
        }
        return publicationTypeList;
    }

    private static Map<PublicationType, Long> generatePublicationTypeOccurenceTally(List<PublicationType> list) {
        return list.stream().collect(Collectors.groupingBy(type -> type, Collectors.counting()));
    }

    private static boolean dataCiteResourceContainsJournalArticle(String resourceType) {
        String uncontrolledResourceType = resourceType.toLowerCase(Locale.ENGLISH);
        return uncontrolledResourceType.contains(JOURNAL) && uncontrolledResourceType.contains(ARTICLE);
    }
}
