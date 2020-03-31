package no.unit.nva.doi.transformer.utils;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import no.unit.nva.model.Pages;
import org.junit.jupiter.api.Test;

public class StringUtilsTest {

    @Test
    public void removeXmlTagsReturnsStringWithNoXmlTags() {
        String input = "<xmlTag> hello world<jap>something else</jap> hello again</xmlTag>";
        String expectedOutput = "hello world something else hello again";
        String actualOutput = StringUtils.removeXmlTags(input);
        assertThat(actualOutput, is(equalTo(expectedOutput)));
    }

    @Test
    public void parsePageShouldReturnABeginAndEndPageForPagesSplitWithDash() {
        String begins = "12";
        String ends = "34";
        String delimiter = "-";
        String pageString = String.join(delimiter, begins, ends);
        Pages expected = new Pages.Builder().withBegins(begins).withEnds(ends).build();
        Pages actual = StringUtils.parsePage(pageString);
        assertThat(actual,is(equalTo(expected)));
    }
}
