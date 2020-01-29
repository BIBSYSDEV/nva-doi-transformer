package no.unit.nva.doi.transformer.model.internal.internal;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EntityType {

    JOURNAL_ARTICLE("JournalArticle");

    private String value;

    EntityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static EntityType lookup(String value) {
        return Arrays
                .stream(values())
                .filter(entityType -> entityType.getValue().equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException());
    }
}
