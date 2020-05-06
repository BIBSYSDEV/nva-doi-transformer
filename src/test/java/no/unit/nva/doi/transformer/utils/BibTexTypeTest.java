package no.unit.nva.doi.transformer.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class BibTexTypeTest {
    @DisplayName("getByType returns NON_EXISTING_TYPE when the type does not exist")
    @Test
    void getByTypeReturnsNullWhenValueDoesNotExist() {
        assertEquals(BibTexType.NON_EXISTING_TYPE, BibTexType.getByType("X"));
    }

    @DisplayName("getByType returns BibTexType when type exists ")
    @ParameterizedTest
    @ValueSource(strings = {"article", "book", "booklet", "conference", "inbook", "incollection", "inproceedings",
            "manual", "mastersthesis", "misc", "phdthesis", "proceedings", "techreport", "unpublished"})
    void getByTypeReturnsBibTexTypeWhenTypeExists(String input) {
        assertNotEquals(BibTexType.NON_EXISTING_TYPE, BibTexType.getByType(input));
    }

}