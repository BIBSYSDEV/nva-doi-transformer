package no.unit.nva.doi.transformer.model.internal.external;

public class DataciteTypes {

    private String ris;
    private String bibtex;
    private String citeproc;
    private String schemaOrg;
    private String resourceType;
    private String resourceTypeGeneral;

    public String getRis() {
        return ris;
    }

    public void setRis(String ris) {
        this.ris = ris;
    }

    public String getBibtex() {
        return bibtex;
    }

    public void setBibtex(String bibtex) {
        this.bibtex = bibtex;
    }

    public String getCiteproc() {
        return citeproc;
    }

    public void setCiteproc(String citeproc) {
        this.citeproc = citeproc;
    }

    public String getSchemaOrg() {
        return schemaOrg;
    }

    public void setSchemaOrg(String schemaOrg) {
        this.schemaOrg = schemaOrg;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceTypeGeneral() {
        return resourceTypeGeneral;
    }

    public void setResourceTypeGeneral(String resourceTypeGeneral) {
        this.resourceTypeGeneral = resourceTypeGeneral;
    }
}
