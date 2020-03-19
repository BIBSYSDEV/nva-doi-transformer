package no.unit.nva.doi.transformer.model.crossrefmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Author {

    @JsonProperty("given")
    private String givenName;
    @JsonProperty("family")
    private String familyName;
    @JsonProperty("sequence")
    private String sequence;
    @JsonProperty("affiliation")
    private List<String> affiliation;

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public List<String> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<String> affiliation) {
        this.affiliation = affiliation;
    }

    public static final class Builder {

        private String givenName;
        private String familyName;
        private String sequence;
        private List<String> affiliation;

        public Builder() {
        }

        public Builder withGivenName(String givenName) {
            this.givenName = givenName;
            return this;
        }

        public Builder withFamilyName(String familyName) {
            this.familyName = familyName;
            return this;
        }

        public Builder withSequence(String sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder withAffiliation(List<String> affiliation) {
            this.affiliation = affiliation;
            return this;
        }

        public Author build() {
            Author author = new Author();
            author.setGivenName(givenName);
            author.setFamilyName(familyName);
            author.setSequence(sequence);
            author.setAffiliation(affiliation);
            return author;
        }
    }
}
