package no.unit.nva.doi.transformer.language;

import java.net.URI;
import java.util.Locale;
import java.util.Optional;
import no.unit.nva.doi.transformer.language.exceptions.LanguageUriNotFoundException;

public interface LanguageDetector {

    String UNEXPECTED_LANGUAGE_ERROR = "Could not find mapping for English";

    Locale DEFAULT_LOCALE = Locale.ENGLISH;

    Locale detectLocale(String input);

    default URI detectLang(String input) throws LanguageUriNotFoundException {
        return LanguageMapper.getUriFromIso639(detectLocale(input).getISO3Language());
    }

    default URI detectLangWithDefault(String input)  {
         try{
             return detectLangOpt(detectLocale(input).getISO3Language())
                 .orElse(LanguageMapper.getUriFromIso639(DEFAULT_LOCALE.getISO3Language()));
         }
         catch(LanguageUriNotFoundException e){
             throw new IllegalStateException(UNEXPECTED_LANGUAGE_ERROR,e);
         }

    }


    default Optional<URI> detectLangOpt(String input)  {
        return LanguageMapper.getUriFromIso639AsOptional(detectLocale(input).getISO3Language());
    }
}

