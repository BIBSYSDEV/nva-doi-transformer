package no.unit.nva.doi.transformer.model.internal.external;

import java.util.List;

public interface Creator {
    String getGivenName();

    void setGivenName(String givenName);

    String getFamilyName();

    void setFamilyName(String familyName);

    List<Affiliation> getAffiliation();

    void setAffiliation(List<Affiliation> affiliation);
}
