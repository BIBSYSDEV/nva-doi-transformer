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


    public String toString(){
        return this.name();
    }
}
