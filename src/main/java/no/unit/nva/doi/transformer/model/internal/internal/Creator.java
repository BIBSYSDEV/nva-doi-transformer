package no.unit.nva.doi.transformer.model.internal.internal;

public class Creator {

    private String identifier;

    private Creator(Builder builder) {
        setIdentifier(builder.identifier);
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public static final class Builder {
        private transient String identifier;

        public Builder withIdentifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Creator build() {
            return new Creator(this);
        }
    }
}
