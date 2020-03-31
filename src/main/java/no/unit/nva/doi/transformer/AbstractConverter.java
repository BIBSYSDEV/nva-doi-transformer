package no.unit.nva.doi.transformer;

import java.net.URI;
import java.util.Locale;
import java.util.stream.Stream;
import no.unit.nva.doi.transformer.language.LanguageDetector;
import no.unit.nva.doi.transformer.language.LanguageMapper;
import no.unit.nva.doi.transformer.utils.TextLang;
import no.unit.nva.model.Organization;
import no.unit.nva.model.PublicationDate;
import no.unit.nva.model.PublicationStatus;

public class AbstractConverter {

    public static final PublicationStatus DEFAULT_NEW_PUBLICATION_STATUS = PublicationStatus.NEW;
    public static final String FAMILY_NAME_GIVEN_NAME_SEPARATOR = ", ";
    public static final String UNEXPECTED_LANGUAGE_ERROR = "Could not find mapping for English";

    protected LanguageDetector languageDetector;

    public AbstractConverter(LanguageDetector detector) {
        this.languageDetector = detector;
    }

    protected String toName(String familyName, String givenName) {
        return String.join(FAMILY_NAME_GIVEN_NAME_SEPARATOR, familyName, givenName);
    }

    protected PublicationDate toDate(Integer publicationYear) {
        return new PublicationDate.Builder()
            .withYear(publicationYear.toString())
            .build();
    }

    protected String getMainTitle(Stream<String> titles) {
        return titles.findFirst().orElse(null);
    }

    protected Organization toPublisher(URI publisherId) {
        return new Organization.Builder()
            .withId(publisherId)
            .build();
    }

    protected TextLang detectLanguage(String title) {
        try {
            return new TextLang(title, languageDetector.detectLang(title));
        } catch (Exception e) {
            URI defaultLang = LanguageMapper.getUriOpt(Locale.ENGLISH.getISO3Language())
                                            .orElseThrow(() -> new RuntimeException(UNEXPECTED_LANGUAGE_ERROR));
            return new TextLang(title, defaultLang);
        }
    }
}
