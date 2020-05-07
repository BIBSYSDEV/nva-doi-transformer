package no.unit.nva.doi.transformer.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DataciteRelatedIdentifierTest {

    @DisplayName("DataciteRelatedIdentifiers exist")
    @ParameterizedTest
    @ValueSource(strings = {"ARK", "ARXIV", "BIBCODE", "DOI", "EAN13", "EISSN", "HANDLE", "IGSN", "ISBN", "ISSN",
            "ISTC", "LISSN", "LSID", "PMID", "PURL", "UPC", "URL", "URN", "W3ID", "UNKNOWN_IDENTIFIER"})
    void dataciteRelatedIdentifiersExist(String value) {
        assertNotNull(DataciteRelatedIdentifier.getByCode(value));
    }

    @DisplayName("DataciteRelatedIdentifiers can be retrieved by code")
    @ParameterizedTest
    @ValueSource(strings = {"ARK", "ARXIV", "BIBCODE", "DOI", "EAN13", "EISSN", "HANDLE", "IGSN", "ISBN", "ISSN",
            "ISTC", "LISSN", "LSID", "PMID", "PURL", "UPC", "URL", "URN", "W3ID"})
    void dataciteRelatedIdentifiersGetByCodeReturnCodeWhenInputCodeIsValid(String value) {
        assertEquals(value, DataciteRelatedIdentifier.getByCode(value).getCode().toUpperCase());
    }

    @DisplayName("DataciteRelatedIdentifiers have description")
    @ParameterizedTest
    @ValueSource(strings = {"ARK", "ARXIV", "BIBCODE", "DOI", "EAN13", "EISSN", "HANDLE", "IGSN", "ISBN", "ISSN",
            "ISTC", "LISSN", "LSID", "PMID", "PURL", "UPC", "URL", "URN", "W3ID"})
    void dataciteRelatedIdentifiersGetDescriptionReturnCodeWhenInputCodeIsValid(String value) {
        assertNotNull(DataciteRelatedIdentifier.getByCode(value).getDescription());
    }

    @DisplayName("DataciteRelatedIdentifiers.UNKNOWN_CODE is returned when code is not known")
    @ParameterizedTest
    @ValueSource(strings = {"FARK", "barXive", "BURL"})
    void dataciteRelatedIdentifiersGetByCodeReturnUnknownCodeWhenInputCodeIsInvalid(String value) {
        assertEquals(DataciteRelatedIdentifier.UNKNOWN_IDENTIFIER, DataciteRelatedIdentifier.getByCode(value));
    }

    @DisplayName("DataciteRelatedIdentifiers.UNKNOWN_CODE is returned when code is null")
    @Test
    void dataciteRelatedIdentifiersGetByCodeReturnUnknownCodeWhenInputCodeIsINull() {
        assertEquals(DataciteRelatedIdentifier.UNKNOWN_IDENTIFIER, DataciteRelatedIdentifier.getByCode(null));
    }
}