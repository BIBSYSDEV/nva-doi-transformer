package no.unit.nva.doi.transformer.utils;

import no.unit.nva.model.PublicationType;

import java.util.Arrays;

import static java.util.Objects.isNull;

public enum BibTexType {

    // See https://en.wikipedia.org/wiki/BibTeX#Entry_types

    ARTICLE("article", PublicationType.JOURNAL_CONTENT),
    BOOK("book", null),
    BOOKLET("booklet", null),
    CONFERENCE("conference", null), // same as inproceedings
    INBOOK("inbook", null),
    INCOLLECTION("incollection", null),
    INPROCEEDINGS("inproceedings", null),
    MANUAL("manual", null),
    MASTERSTHESIS("mastersthesis", null),
    MISC("misc", null),
    PHDTHESIS("phdthesis", null),
    PROCEEDINGS("proceedings", null),
    TECHREPORT("techreport", null),
    UNPUBLISHED("unpublished", null),
    NON_EXISTING_TYPE(null, null);

    private final String type;
    private final PublicationType publicationType;

    BibTexType(String type, PublicationType publicationType) {
        this.type = type;
        this.publicationType = publicationType;
    }

    public String getType() {
        return this.type;
    }

    public PublicationType getPublicationType() {
        return this.publicationType;
    }

    /**
     * Retrieve the PublicationType based on a BibTeX type string.
     *
     * @param type the BibTeX type string.
     * @return a PublicationType.
     */
    public static BibTexType getByType(String type) {
        if (isNull(type)) {
            return NON_EXISTING_TYPE;
        }

        return Arrays.stream(values())
                .filter(bibTexType -> !bibTexType.equals(BibTexType.NON_EXISTING_TYPE))
                .filter(bibTexType -> bibTexType.getType().equals(type))
                .findFirst()
                .orElse(NON_EXISTING_TYPE);
    }
}
