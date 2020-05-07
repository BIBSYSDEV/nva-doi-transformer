package no.unit.nva.doi.transformer.utils;

import java.util.Arrays;

import static java.util.Objects.isNull;

public enum DataciteRelatedIdentifier {
    ARK("ARK", "Archival Resource Key"),
    ARXIV("arXiv", "arXiv identifier"),
    BIBCODE("bibcode", "Astrophysics Data System bibliographic code"),
    DOI("DOI", "Digital Object Identifier"),
    EAN13("EAN13", "European Article Number"),
    EISSN("EISSN", "Electronic International Standard Serial Number"),
    HANDLE("Handle", "HNDL, Handle"),
    IGSN("IGSN", "International Geo Sample Number"),
    ISBN("ISBN", "International Standard Book Number"),
    ISSN("ISSN", "International Standard Serial Number"),
    ISTC("ISTC", "International Standard Text Code"),
    LISSN("LISSN", "Linking ISSN, or ISSN-L"),
    LSID("LSID", "Life Science Identifier"),
    PMID("PMID", "PubMed identifier"),
    PURL("PURL", "Persistent URL"),
    UPC("UPC", "Universal Product Code"),
    URL("URL", "Uniform Resource Locator"),
    URN("URN", "Uniform Resource Name"),
    W3ID("w3id", "Permanent Web-Application identifier"),
    UNKNOWN_IDENTIFIER(null, null);

    private String code;
    private String description;

    DataciteRelatedIdentifier(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static DataciteRelatedIdentifier getByCode(String code) {
        if (isNull(code)) {
            return UNKNOWN_IDENTIFIER;
        }

        return Arrays.stream(values())
                .filter(DataciteRelatedIdentifier::isKnown)
                .filter(dataciteRelatedIdentifier -> dataciteRelatedIdentifier.getCode().equalsIgnoreCase(code))
                .collect(SingletonCollector.collectOrElse(UNKNOWN_IDENTIFIER));
    }

    private static boolean isKnown(DataciteRelatedIdentifier dataciteRelatedIdentifier) {
        return !dataciteRelatedIdentifier.equals(DataciteRelatedIdentifier.UNKNOWN_IDENTIFIER);
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

