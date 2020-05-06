package no.unit.nva.doi.transformer.model.internal.external;

import no.unit.nva.model.PublicationType;

import java.net.URL;
import java.util.List;

public interface SourceDocument {
    String getDoi();

    void setDoi(String doi);

    URL getUrl();

    void setUrl(URL url);

    PublicationType getType();

    List<Creator> getCreators();

    void setCreators(List<Creator> creators);

    List<DataciteTitle> getTitles();

    void setTitles(List<DataciteTitle> titles);

    String getPublisher();

    void setPublisher(String publisher);

    DataciteContainer getContainer();

    void setContainer(DataciteContainer container);

    List<DataciteDate> getDates();

    void setDates(List<DataciteDate> dates);

    Integer getPublicationYear();

    void setPublicationYear(Integer publicationYear);
}
