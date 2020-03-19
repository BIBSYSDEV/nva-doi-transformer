package no.unit.nva.doi.transformer;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;
import no.unit.nva.doi.transformer.model.internal.external.DataciteCreator;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.model.Publication;
import org.junit.Assert;
import org.junit.Test;

public class DataciteResponseConverterTest {

    private ObjectMapper objectMapper = MainHandler.createObjectMapper();

    @Test
    public void test() throws IOException {

        DataciteResponse dataciteResponse = objectMapper.readValue(
            new File("src/test/resources/datacite_response.json"), DataciteResponse.class);

        DataciteResponseConverter converter = new DataciteResponseConverter();
        Publication publication = converter.toPublication(dataciteResponse, UUID.randomUUID(), "junit",
            URI.create("http://example.org/123"));

        String json = objectMapper.writeValueAsString(publication);

        System.out.println(json);
        Assert.assertNotNull(json);
    }

    @Test
    public void testToName() {
        DataciteCreator dataciteCreator = new DataciteCreator();
        dataciteCreator.setFamilyName("Family");
        dataciteCreator.setGivenName("Given");
        DataciteResponseConverter converter = new DataciteResponseConverter();

        String name = converter.toName(dataciteCreator);

        Assert.assertEquals("Family, Given", name);
    }
}
