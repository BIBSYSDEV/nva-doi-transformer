package no.unit.nva.doi.transformer.utils;

import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.external.DataciteTypes;
import no.unit.nva.model.PublicationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataciteTypesUtilTest {

    public static final String DATASET = "Dataset";

    @DisplayName("The mapper util checks for resourceTypeGeneral and returns null for non-text values")
    @Test
    void testMapperReturnsNullWhenInputIsNotTypeText() {
        DataciteTypesUtil.mapToType(getNonTextDataciteResource());
    }

    @DisplayName("The mapping utility returns type JOURNAL_CONTENT when all types agree")
    @Test
    void mapToTypeReturnsJournalContentWhenAllTypesAreJournalContent() {
        DataciteTypes dataciteTypes = new DataciteTypes.Builder()
                .withBibtex(BibTexType.ARTICLE.getType())
                .withCiteproc(CiteProcType.ARTICLE_JOURNAL.getType())
                .withResourceType("JournalArticle")
                .withRis(RisType.JOUR.name())
                .withSchemaOrg(SchemaOrgType.SCHOLARLY_ARTICLE.getType())
                .withResourceTypeGeneral("Text")
                .build();
        DataciteResponse dataciteResponse = new DataciteResponse.Builder()
                .withTypes(dataciteTypes)
                .build();
        assertEquals(PublicationType.JOURNAL_CONTENT, DataciteTypesUtil.mapToType(dataciteResponse));

    }

    @DisplayName("The mapping utility returns type JOURNAL_CONTENT when all but one types agree")
    @Test
    void mapToTypeReturnsJournalContentWhenAllButOneTypesAreJournalContent() {
        DataciteTypes dataciteTypes = new DataciteTypes.Builder()
                .withBibtex(BibTexType.CONFERENCE.getType())
                .withCiteproc(CiteProcType.ARTICLE_JOURNAL.getType())
                .withResourceType("JournalArticle")
                .withRis(RisType.JOUR.name())
                .withSchemaOrg(SchemaOrgType.SCHOLARLY_ARTICLE.getType())
                .withResourceTypeGeneral("Text")
                .build();
        DataciteResponse dataciteResponse = new DataciteResponse.Builder()
                .withTypes(dataciteTypes)
                .build();
        assertEquals(PublicationType.JOURNAL_CONTENT, DataciteTypesUtil.mapToType(dataciteResponse));
    }

    @DisplayName("The mapping utility returns type JOURNAL_CONTENT when all but two types agree")
    @Test
    void mapToTypeReturnsJournalContentWhenAllButTwoTypesAreJournalContent() {
        DataciteTypes dataciteTypes = new DataciteTypes.Builder()
                .withBibtex(BibTexType.CONFERENCE.getType())
                .withCiteproc(CiteProcType.PAPER_CONFERENCE.getType())
                .withResourceType("JournalArticle")
                .withRis(RisType.JOUR.name())
                .withSchemaOrg(SchemaOrgType.SCHOLARLY_ARTICLE.getType())
                .withResourceTypeGeneral("Text")
                .build();
        DataciteResponse dataciteResponse = new DataciteResponse.Builder()
                .withTypes(dataciteTypes)
                .build();
        assertEquals(PublicationType.JOURNAL_CONTENT, DataciteTypesUtil.mapToType(dataciteResponse));
    }

    @DisplayName("The mapping utility returns type JOURNAL_CONTENT when two types agree")
    @Test
    void mapToTypeReturnsJournalContentWhenTwoTypesAreJournalContent() {
        DataciteTypes dataciteTypes = new DataciteTypes.Builder()
                .withBibtex(BibTexType.CONFERENCE.getType())
                .withCiteproc(CiteProcType.PAPER_CONFERENCE.getType())
                .withResourceType("JournalArticle")
                .withSchemaOrg(SchemaOrgType.SCHOLARLY_ARTICLE.getType())
                .withResourceTypeGeneral("Text")
                .build();
        DataciteResponse dataciteResponse = new DataciteResponse.Builder()
                .withTypes(dataciteTypes)
                .build();
        assertEquals(PublicationType.JOURNAL_CONTENT, DataciteTypesUtil.mapToType(dataciteResponse));
    }


    private DataciteResponse getNonTextDataciteResource() {
        DataciteTypes dataciteType = new DataciteTypes.Builder()
                .withResourceType(DATASET)
                .build();
        return new DataciteResponse.Builder()
                .withTypes(dataciteType)
                .build();
    }

}