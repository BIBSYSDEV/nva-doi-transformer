package no.unit.nva.doi.transformer.model.crossrefmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

    @JsonProperty("status")
    private String status;
    @JsonProperty("message-type")
    private String messageType;
    @JsonProperty("message-version")
    private String messageVersion;
    @JsonProperty("message")
    private CrossRefDocument crossRefDocument;

    public String getStatus() {
        return status;
    }

    public void setStatus(String input) {
        this.status = input;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String input) {
        this.messageType = input;
    }

    public String getMessageVersion() {
        return messageVersion;
    }

    public void setMessageVersion(String input) {
        this.messageVersion = input;
    }

    public CrossRefDocument getCrossRefDocument() {
        return crossRefDocument;
    }

    public void setCrossRefDocument(CrossRefDocument input) {
        this.crossRefDocument = input;
    }
}

