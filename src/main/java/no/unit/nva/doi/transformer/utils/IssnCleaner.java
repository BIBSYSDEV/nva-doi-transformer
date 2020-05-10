package no.unit.nva.doi.transformer.utils;

import no.unit.nva.model.validator.IssnValidator;

import static java.util.Objects.isNull;

public class IssnCleaner {

    public static final String DELIMITER = "-";
    public static final String VALID_CHARS = "[^[0-9]Xx]+";
    public static final String EMPTY_STRING = "";
    public static final int CLEANED_LENGTH = 8;
    public static final int BEGIN_FIRST_PART = 0;
    public static final int END_FIRST_PART = 4;
    public static final int BEGIN_SECOND_PART = 4;

    /**
     * Takes an input string that is an ISSN candidate, tests it and formats it if possible or returns null.
     * @param value a string ISSN candidate.
     * @return A string of a valid ISSN, or null.
     */
    public static String clean(String value) {

        String issnCandidate = reformat(value);

        if (isNull(issnCandidate)) {
            return null;
        }

        return IssnValidator.validate(issnCandidate) ? issnCandidate : null;

    }

    private static String reformat(String value) {
        if (isNull(value)) {
            return null;
        }

        String cleanedString = StringUtils.removeMultipleWhiteSpaces(value).replaceAll(VALID_CHARS, EMPTY_STRING);

        if (cleanedString.length() != CLEANED_LENGTH) {
            return null;
        }

        return cleanedString.substring(BEGIN_FIRST_PART, END_FIRST_PART)
                + DELIMITER
                + cleanedString.substring(BEGIN_SECOND_PART);
    }


}