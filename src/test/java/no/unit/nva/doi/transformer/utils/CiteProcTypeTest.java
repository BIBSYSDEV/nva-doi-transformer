package no.unit.nva.doi.transformer.utils;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CiteProcTypeTest {
    @DisplayName("getByType returns NON_EXISTING_TYPE when the type does not exist")
    @Test
    void getByTypeReturnsNullWhenValueDoesNotExist() {
        assertEquals(CiteProcType.NON_EXISTING_TYPE, CiteProcType.getByType("X"));
    }

    @DisplayName("getByType returns CiteProcType when type exists ")
    @ParameterizedTest
    @ValueSource(strings = {"article", "article-journal", "article-magazine", "article-newspaper", "bill", "book",
            "broadcast", "chapter", "dataset", "entry", "entry-dictionary", "entry-encyclopedia", "figure", "graphic",
            "interview", "legal_case", "legislation", "manuscript", "map", "motion_picture", "musical_score",
            "pamphlet", "paper-conference", "patent", "personal_communication", "post", "post-weblog", "report",
            "review", "review-book", "song", "speech", "thesis", "treaty", "webpage"})
    void getByTypeReturnsCiteProcTypeWhenTypeExists(String input) {
        assertNotEquals(CiteProcType.NON_EXISTING_TYPE, CiteProcType.getByType(input));
    }

}