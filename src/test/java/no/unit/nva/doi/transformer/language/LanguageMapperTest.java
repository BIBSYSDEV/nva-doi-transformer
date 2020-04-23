package no.unit.nva.doi.transformer.language;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LanguageMapperTest {

    private static final URI expected = URI.create("http://lexvo.org/id/iso639-3/eng");

    @Test
    public void getUriFromIso639ReturnsUriForTwoLetterLanguageCodes() {
        Optional<URI> uri = LanguageMapper.getUriFromIsoAsOptional("en");
        assertThat(uri.isPresent(), is(equalTo(true)));
        assertThat(uri.get(), is(equalTo(expected)));
    }

    @Test
    public void getUriFromIso639ReturnsUriForIso639LanguageCodes() {
        Optional<URI> uri = LanguageMapper.getUriFromIsoAsOptional("eng");
        assertThat(uri.isPresent(), is(equalTo(true)));
        assertThat(uri.get(), is(equalTo(expected)));
    }
}