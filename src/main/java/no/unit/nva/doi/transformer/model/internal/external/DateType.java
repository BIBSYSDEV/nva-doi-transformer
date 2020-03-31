package no.unit.nva.doi.transformer.model.internal.external;

public enum DateType {
    ACCEPTED,
    AVAILABLE,
    COPYRIGHTED,
    COLLECTED,
    CREATED,
    ISSUED,
    SUBMITTED,
    UPDATED,
    VALID,
    WITHDRAWN,
    OTHER;

    @Override
    public String toString() {
        return this.name();
    }
}
