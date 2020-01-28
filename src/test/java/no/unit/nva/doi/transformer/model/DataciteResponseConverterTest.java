package no.unit.nva.doi.transformer.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.doi.transformer.DataciteResponseConverter;
import no.unit.nva.doi.transformer.MainHandler;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.internal.Resource;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class DataciteResponseConverterTest {


    private ObjectMapper objectMapper = MainHandler.createObjectMapper();

    @Test
    public void test() throws IOException {

        DataciteResponse dataciteResponse = objectMapper.readValue(new File("src/test/resources/datacite_response.json"), DataciteResponse.class);

        DataciteResponseConverter converter = new DataciteResponseConverter();
        Resource resource = converter.toResource(dataciteResponse, "123", "owner");

        String json = objectMapper.writeValueAsString(resource);

        System.out.println(json);

    }

}
