package no.unit.nva.doi.transformer.model.internal.external;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class DataciteResponse {

    private URL id;
    private String doi;
    private URL url;
    private DataciteTypes types;
    private List<DataciteCreator> creators;
    private List<DataciteTitle> titles;
    private String publisher;
    private DataciteContainer container;
    private List<DataciteContributor> contributors;
    private List<DataciteDate> dates;
    private Integer publicationYear;
    private List<DataciteIdentifier> identifiers;
    private List<DataciteRelatedIdentifier> relatedIdentifiers;
    private String schemaVersion;
    private String providerId;
    private String clientId;
    private String agency;
    private String state;

    public URL getId() {
        return id;
    }

    public void setId(URL id) {
        this.id = id;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public DataciteTypes getTypes() {
        return types;
    }

    public void setTypes(DataciteTypes types) {
        this.types = types;
    }

    public List<DataciteCreator> getCreators() {
        return creators;
    }

    public void setCreators(List<DataciteCreator> creators) {
        this.creators = creators;
    }

    public List<DataciteTitle> getTitles() {
        return titles;
    }

    public void setTitles(List<DataciteTitle> titles) {
        this.titles = titles;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public DataciteContainer getContainer() {
        return container;
    }

    public void setContainer(DataciteContainer container) {
        this.container = container;
    }

    public List<DataciteContributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<DataciteContributor> contributors) {
        this.contributors = contributors;
    }

    public List<DataciteDate> getDates() {
        return dates;
    }

    public void setDates(List<DataciteDate> dates) {
        this.dates = dates;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public List<DataciteIdentifier> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<DataciteIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public List<DataciteRelatedIdentifier> getRelatedIdentifiers() {
        return relatedIdentifiers;
    }

    public void setRelatedIdentifiers(List<DataciteRelatedIdentifier> relatedIdentifiers) {
        this.relatedIdentifiers = relatedIdentifiers;
    }

    public String getSchemaVersion() {
        return schemaVersion;
    }

    public void setSchemaVersion(String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
