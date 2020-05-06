package no.unit.nva.doi.transformer.utils;

import no.unit.nva.model.PublicationType;

public enum RisType {
    ABST("Abstract", null),
    ADVS("Audiovisual material", null),
    ART("Art Work", null),
    BILL("Bill / Resolution", null),
    BOOK("Book, Whole", null),
    CASE("Case", null),
    CHAP("Book chapter", null),
    COMP("Computer program", null),
    CONF("Conference proceeding", null),
    CTLG("Catalog", null),
    DATA("Data file", null),
    ELEC("Electronic Citation", null),
    GEN("Generic", null),
    HEAR("Hearing", null),
    ICOMM("Internet Communication", null),
    INPR("In Press", null),
    JFULL("Journal(full)", null),
    JOUR("Journal", PublicationType.JOURNAL_CONTENT),
    MAP("Map", null),
    MGZN("Magazine article", null),
    MPCT("Motion picture", null),
    MUSIC("Music score", null),
    NEWS("Newspaper", null),
    PAMP("Pamphlet", null),
    PAT("Patent", null),
    PCOMM("Personal communication", null),
    RPRT("Report", null),
    SER("Serial(Book, Monograph)", null),
    SLIDE("Slide", null),
    SOUND("Sound recording", null),
    STAT("Statute", null),
    THES("Thesis / Dissertation", null),
    UNBILL("Unenacted bill / resolution", null),
    UNPB("Unpublished work", null),
    VIDEO("Video recording", null),
    NON_EXISTING_TYPE(null, null);

    private final String type;
    private final PublicationType publicationType;

    RisType(String type, PublicationType publicationType) {
        this.type = type;
        this.publicationType = publicationType;
    }

    public static RisType getByType(String risType) {
        try {
            return valueOf(risType);
        } catch (IllegalArgumentException | NullPointerException e) {
            return NON_EXISTING_TYPE;
        }
    }

    public PublicationType getPublicationType() {
        return this.publicationType;
    }
}
