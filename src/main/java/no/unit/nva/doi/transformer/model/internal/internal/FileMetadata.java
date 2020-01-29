package no.unit.nva.doi.transformer.model.internal.internal;

public class FileMetadata {

    private String filename;
    private String mimeType;
    private String checksum;
    private String size;

    private FileMetadata(Builder builder) {
        setFilename(builder.filename);
        setMimeType(builder.mimeType);
        setChecksum(builder.checksum);
        setSize(builder.size);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public static final class Builder {
        private transient String filename;
        private transient String mimeType;
        private transient String checksum;
        private transient String size;

        public Builder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public Builder withChecksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public Builder withSize(String size) {
            this.size = size;
            return this;
        }

        public FileMetadata build() {
            return new FileMetadata(this);
        }
    }
}
