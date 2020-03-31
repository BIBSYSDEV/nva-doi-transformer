package no.unit.nva.doi.transformer.language;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import no.unit.nva.doi.transformer.language.exceptions.LanguageUriNotFoundException;

public interface LanguageDetector {

    Locale detectLocale(String input);

    default URI detectLang(String input) throws LanguageUriNotFoundException {
        return LanguageMapper.getUri(detectLocale(input).getISO3Language());
    }

    default Optional<URI> detectLangOpt(String input)  {
        return LanguageMapper.getUriOpt(detectLocale(input).getISO3Language());
    }
}

