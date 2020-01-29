package no.unit.nva.doi.transformer.model.internal.internal;

import java.util.List;
import java.util.Map;

public class EntityDescription {

    private EntityType type;
    private Map<String, String> titles;
    private PublicationDate date;
    private List<Contributor> contributors;

    public EntityDescription() {

    }

    private EntityDescription(Builder builder) {
        setType(builder.type);
        setTitles(builder.titles);
        setDate(builder.date);
        setContributors(builder.contributors);
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public PublicationDate getDate() {
        return date;
    }

    public void setDate(PublicationDate date) {
        this.date = date;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    public static final class Builder {
        private EntityType type;
        private Map<String, String> titles;
        private PublicationDate date;
        private List<Contributor> contributors;

        public Builder() {
        }

        public Builder withType(EntityType type) {
            this.type = type;
            return this;
        }

        public Builder withTitles(Map<String, String> titles) {
            this.titles = titles;
            return this;
        }

        public Builder withDate(PublicationDate date) {
            this.date = date;
            return this;
        }

        public Builder withContributors(List<Contributor> contributors) {
            this.contributors = contributors;
            return this;
        }

        public EntityDescription build() {
            return new EntityDescription(this);
        }
    }
}
