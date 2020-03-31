package no.unit.nva.doi.transformer.utils;

import no.unit.nva.model.Pages;

public final class StringUtils {

    public static final String XML_TAG_REGEX_GREEDY_MATCHER = "<[^<>]+>";
    public static final String SPACE = " ";
    public static final String NOT_DIGIT = "\\D";
    public static final String DOUBLE_WHITESPACE = "\\s\\s";

    public static String removeXmlTags(String input) {
        String noXmlTags = input.replaceAll(XML_TAG_REGEX_GREEDY_MATCHER, SPACE);
        String removeMultipleWhitespaces = removeMultipleWhiteSpaces(noXmlTags);
        return removeMultipleWhitespaces.trim();
    }

    public static String removeMultipleWhiteSpaces(String input) {
        String buffer = input.trim();
        String result = buffer.replaceAll(DOUBLE_WHITESPACE, SPACE);
        while (!result.equals(buffer)) {
            buffer = result;
            result = buffer.replaceAll(DOUBLE_WHITESPACE, SPACE);
        }
        return result;
    }

    public static Pages parsePage(String pages) {
        if (pages == null) {
            return null;
        }

        String[] array = pages.replaceAll(NOT_DIGIT, SPACE)
                              .trim()
                              .split(SPACE);
        String start = null;
        String end = null;
        if (isNotEmpty(array)) {
            start = array[0];
            if (hasSecondArg(array)) {
                end = array[1];
            }
        }
        return new Pages.Builder().withBegins(start).withEnds(end).build();
    }

    private static boolean hasSecondArg(String[] array) {
        return array.length > 1;
    }

    private static boolean isNotEmpty(String[] array) {
        return array.length > 0;
    }
}
