package no.unit.nva.doi.transformer.language;

import java.net.URI;
import java.util.Locale;

public interface LanguageDetector {

    Locale detectLocale(String input);

    default URI detectLang(String input) {
        return LanguageMapper.getURI(detectLocale(input).getISO3Language());
    }
}

