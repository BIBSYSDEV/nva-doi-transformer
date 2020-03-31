package no.unit.nva.doi.transformer.utils;

import java.net.URI;

public class TextLang {

    private String text;
    private URI language;

    public TextLang(String text, URI language) {
        this.text=text;
        this.language=language;
    }

    public String getText() {
        return text;
    }

    public URI getLanguage() {
        return language;
    }
}
