package no.unit.nva.doi.transformer.model.internal.internal;

import java.util.List;

public class Contributor {

    private String arpId;
    private String orcId;
    private String name;
    private NameType nameType;
    private List<String> affiliation;
    private Integer sequence;

    public Contributor() {

    }

    private Contributor(Builder builder) {
        setArpId(builder.arpId);
        setOrcId(builder.orcId);
        setName(builder.name);
        setNameType(builder.nameType);
        setAffiliation(builder.affiliation);
        setSequence(builder.sequence);
    }

    public String getArpId() {
        return arpId;
    }

    public void setArpId(String arpId) {
        this.arpId = arpId;
    }

    public String getOrcId() {
        return orcId;
    }

    public void setOrcId(String orcId) {
        this.orcId = orcId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NameType getNameType() {
        return nameType;
    }

    public void setNameType(NameType nameType) {
        this.nameType = nameType;
    }

    public List<String> getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(List<String> affiliation) {
        this.affiliation = affiliation;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

    public static final class Builder {
        private String arpId;
        private String orcId;
        private String name;
        private NameType nameType;
        private List<String> affiliation;
        private Integer sequence;

        public Builder() {
        }

        public Builder withArpId(String arpId) {
            this.arpId = arpId;
            return this;
        }

        public Builder withOrcId(String orcId) {
            this.orcId = orcId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withNameType(NameType nameType) {
            this.nameType = nameType;
            return this;
        }

        public Builder withAffiliation(List<String> affiliation) {
            this.affiliation = affiliation;
            return this;
        }

        public Builder withSequence(Integer sequence) {
            this.sequence = sequence;
            return this;
        }

        public Contributor build() {
            return new Contributor(this);
        }
    }
}
