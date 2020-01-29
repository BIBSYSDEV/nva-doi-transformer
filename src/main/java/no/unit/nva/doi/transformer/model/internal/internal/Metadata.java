package no.unit.nva.doi.transformer.model.internal.internal;

import java.util.List;
import java.util.Map;

public class Metadata {

    private List<Creator> creators;
    private String handle;
    private String licenseIdentifier;
    private String publicationYear;
    private String publisher;
    private Map<String,String> titles;
    private String resourceType;

    public Metadata() {

    }

    private Metadata(Builder builder) {
        setCreators(builder.creators);
        setHandle(builder.handle);
        setLicenseIdentifier(builder.licenseIdentifier);
        setPublicationYear(builder.publicationYear);
        setPublisher(builder.publisher);
        setTitles(builder.titles);
        setResourceType(builder.resourceType);
    }

    public List<Creator> getCreators() {
        return creators;
    }

    public void setCreators(List<Creator> creators) {
        this.creators = creators;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getLicenseIdentifier() {
        return licenseIdentifier;
    }

    public void setLicenseIdentifier(String licenseIdentifier) {
        this.licenseIdentifier = licenseIdentifier;
    }

    public String getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Map<String, String> getTitles() {
        return titles;
    }

    public void setTitles(Map<String, String> titles) {
        this.titles = titles;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public static final class Builder {
        private transient List<Creator> creators;
        private transient String handle;
        private transient String licenseIdentifier;
        private transient String publicationYear;
        private transient String publisher;
        private transient Map<String, String> titles;
        private transient String resourceType;

        public Builder withCreators(List<Creator> creators) {
            this.creators = creators;
            return this;
        }

        public Builder withHandle(String handle) {
            this.handle = handle;
            return this;
        }

        public Builder withLicenseIdentifier(String licenseIdentifier) {
            this.licenseIdentifier = licenseIdentifier;
            return this;
        }

        public Builder withPublicationYear(String publicationYear) {
            this.publicationYear = publicationYear;
            return this;
        }

        public Builder withPublisher(String publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder withTitles(Map<String, String> titles) {
            this.titles = titles;
            return this;
        }

        public Builder withResourceType(String resourceType) {
            this.resourceType = resourceType;
            return this;
        }

        public Metadata build() {
            return new Metadata(this);
        }
    }
}
