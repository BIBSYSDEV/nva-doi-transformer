package no.unit.nva.doi.transformer.model.internal.internal;

import java.util.Map;

public class Resource {

    private String resourceIdentifier;
    private String modifiedDate;
    private String createdDate;
    private Metadata metadata;
    private Map<String, FileMetadata> files;
    private String owner;
    private String status;
    private String indexedDate;
    private String publishedDate;

    /**
     * Constructor for Resource.
     *
     * @param resourceIdentifier    resourceIdentifier
     * @param modifiedDate  modifiedDate
     * @param createdDate   createdDate
     * @param metadata  metadata
     * @param files files
     * @param owner owner
     * @param status    status
     * @param indexedDate   indexedDate
     * @param publishedDate publishedDate
     */
    public Resource(String resourceIdentifier, String modifiedDate, String createdDate, Metadata metadata,
                    Map<String, FileMetadata> files, String owner, String status, String indexedDate,
                    String publishedDate) {
        this.resourceIdentifier = resourceIdentifier;
        this.modifiedDate = modifiedDate;
        this.createdDate = createdDate;
        this.metadata = metadata;
        this.files = files;
        this.owner = owner;
        this.status = status;
        this.indexedDate = indexedDate;
        this.publishedDate = publishedDate;
    }

    private Resource(Builder builder) {
        setResourceIdentifier(builder.resourceIdentifier);
        setModifiedDate(builder.modifiedDate);
        setCreatedDate(builder.createdDate);
        setMetadata(builder.metadata);
        setFiles(builder.files);
        setOwner(builder.owner);
        setStatus(builder.status);
        setIndexedDate(builder.indexedDate);
        setPublishedDate(builder.publishedDate);
    }

    public String getResourceIdentifier() {
        return resourceIdentifier;
    }

    public void setResourceIdentifier(String resourceIdentifier) {
        this.resourceIdentifier = resourceIdentifier;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Map<String, FileMetadata> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FileMetadata> files) {
        this.files = files;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndexedDate() {
        return indexedDate;
    }

    public void setIndexedDate(String indexedDate) {
        this.indexedDate = indexedDate;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public static final class Builder {
        private String resourceIdentifier;
        private String modifiedDate;
        private String createdDate;
        private Metadata metadata;
        private Map<String, FileMetadata> files;
        private String owner;
        private String status;
        private String indexedDate;
        private String publishedDate;

        public Builder() {
        }

        public Builder withResourceIdentifier(String resourceIdentifier) {
            this.resourceIdentifier = resourceIdentifier;
            return this;
        }

        public Builder withModifiedDate(String modifiedDate) {
            this.modifiedDate = modifiedDate;
            return this;
        }

        public Builder withCreatedDate(String createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withMetadata(Metadata metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder withFiles(Map<String, FileMetadata> files) {
            this.files = files;
            return this;
        }

        public Builder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder withStatus(String status) {
            this.status = status;
            return this;
        }

        public Builder withIndexedDate(String indexedDate) {
            this.indexedDate = indexedDate;
            return this;
        }

        public Builder withPublishedDate(String publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public Resource build() {
            return new Resource(this);
        }
    }
}
