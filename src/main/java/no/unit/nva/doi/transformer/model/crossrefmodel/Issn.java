package no.unit.nva.doi.transformer.model.crossrefmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class Issn {
    @JsonProperty("value")
    private String issn;
    @JsonProperty("type")
    private IssnType type;

    public String getIssn() {
        return issn;
    }

    public void setIssn(String issn) {
        this.issn = issn;
    }

    public IssnType getType() {
        return type;
    }

    public void setType(String type) {
        this.type = IssnType.getType(type);
    }

    private enum IssnType {
        PRINT("print"),
        ELECTRONIC("electronic");

        private final String name;

        IssnType(String name) {
            this.name = name;
        }

        public static IssnType getType(String name) {
            return Arrays.stream(values()).filter(issnType -> issnType.name.equals(name)).findFirst()
                    .orElseThrow(RuntimeException::new);
        }

        public String getName() {
            return name;
        }
    }
}
