package no.unit.nva.doi.transformer.model.crossrefmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Affiliation {

    @JsonProperty("name")
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
