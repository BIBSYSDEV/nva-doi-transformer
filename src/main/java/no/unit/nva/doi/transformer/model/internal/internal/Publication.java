package no.unit.nva.doi.transformer.model.internal.internal;

import java.net.URI;
import java.time.Instant;
import java.util.UUID;

public class Publication {

    private Instant createdDate;
    private PublicationStatus status;
    private URI handle;
    private Instant publishedDate;
    private Instant modifiedDate;
    private String owner;
    private Instant indexedDate;
    private UUID identifier;
    private URI link;
    private Publisher publisher;
    private EntityDescription entityDescription;
    private License license;
    private FileSet fileSet;

    public Publication() {

    }

    private Publication(Builder builder) {
        setCreatedDate(builder.createdDate);
        setStatus(builder.status);
        setHandle(builder.handle);
        setPublishedDate(builder.publishedDate);
        setModifiedDate(builder.modifiedDate);
        setOwner(builder.owner);
        setIndexedDate(builder.indexedDate);
        setIdentifier(builder.identifier);
        setLink(builder.link);
        setPublisher(builder.publisher);
        setEntityDescription(builder.entityDescription);
        setLicense(builder.license);
        setFileSet(builder.fileSet);
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public void setStatus(PublicationStatus status) {
        this.status = status;
    }

    public URI getHandle() {
        return handle;
    }

    public void setHandle(URI handle) {
        this.handle = handle;
    }

    public Instant getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Instant publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Instant getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Instant modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Instant getIndexedDate() {
        return indexedDate;
    }

    public void setIndexedDate(Instant indexedDate) {
        this.indexedDate = indexedDate;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    public URI getLink() {
        return link;
    }

    public void setLink(URI link) {
        this.link = link;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public EntityDescription getEntityDescription() {
        return entityDescription;
    }

    public void setEntityDescription(EntityDescription entityDescription) {
        this.entityDescription = entityDescription;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public FileSet getFileSet() {
        return fileSet;
    }

    public void setFileSet(FileSet fileSet) {
        this.fileSet = fileSet;
    }


    public static final class Builder {
        private Instant createdDate;
        private PublicationStatus status;
        private URI handle;
        private Instant publishedDate;
        private Instant modifiedDate;
        private String owner;
        private Instant indexedDate;
        private UUID identifier;
        private URI link;
        private Publisher publisher;
        private EntityDescription entityDescription;
        private License license;
        private FileSet fileSet;

        public Builder() {
        }

        public Builder withCreatedDate(Instant createdDate) {
            this.createdDate = createdDate;
            return this;
        }

        public Builder withStatus(PublicationStatus status) {
            this.status = status;
            return this;
        }

        public Builder withHandle(URI handle) {
            this.handle = handle;
            return this;
        }

        public Builder withPublishedDate(Instant publishedDate) {
            this.publishedDate = publishedDate;
            return this;
        }

        public Builder withModifiedDate(Instant modifiedDate) {
            this.modifiedDate = modifiedDate;
            return this;
        }

        public Builder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder withIndexedDate(Instant indexedDate) {
            this.indexedDate = indexedDate;
            return this;
        }

        public Builder withIdentifier(UUID identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withLink(URI link) {
            this.link = link;
            return this;
        }

        public Builder withPublisher(Publisher publisher) {
            this.publisher = publisher;
            return this;
        }

        public Builder withEntityDescription(EntityDescription entityDescription) {
            this.entityDescription = entityDescription;
            return this;
        }

        public Builder withLicense(License license) {
            this.license = license;
            return this;
        }

        public Builder withFileSet(FileSet fileSet) {
            this.fileSet = fileSet;
            return this;
        }

        public Publication build() {
            return new Publication(this);
        }
    }
}
