package no.unit.nva.doi.transformer;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.model.Publication;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.singletonMap;
import static no.unit.nva.doi.transformer.MainHandler.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MainHandlerTest {

    private ObjectMapper objectMapper = MainHandler.createObjectMapper();

    private Environment environment;

    @Before
    public void setUp() {
        environment = Mockito.mock(Environment.class);
        Mockito.when(environment.get("ALLOWED_ORIGIN")).thenReturn(Optional.of("*"));
    }

    @Rule
    public final EnvironmentVariables environmentVariables
            = new EnvironmentVariables();

    @Test
    public void testDefaultConstructor() {
        environmentVariables.set("ALLOWED_ORIGIN", "*");
        MainHandler findChannelFunctionApp = new MainHandler();
        assertNotNull(findChannelFunctionApp);
    }

    @Test
    public void testOkResponse() throws IOException {
        DataciteResponseConverter converter = new DataciteResponseConverter();
        Context context = getMockContext();
        MainHandler mainHandler = new MainHandler(objectMapper, converter, environment);
        OutputStream output = new ByteArrayOutputStream();

        mainHandler.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_OK, gatewayResponse.getStatusCode());
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains(CONTENT_TYPE));
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains(ACCESS_CONTROL_ALLOW_ORIGIN));
        Publication publication = objectMapper.readValue(gatewayResponse.getBody().toString(),
                Publication.class);
        assertEquals(DataciteResponseConverter.DEFAULT_NEW_PUBLICATION_STATUS, publication.getStatus());

    }

    @Test
    public void testBadRequestresponse() throws IOException {
        DataciteResponseConverter converter = new DataciteResponseConverter();
        Context context = getMockContext();
        MainHandler mainHandler = new MainHandler(objectMapper, converter, environment);
        OutputStream output = new ByteArrayOutputStream();

        mainHandler.handleRequest(new ByteArrayInputStream(new byte[0]), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_BAD_REQUEST, gatewayResponse.getStatusCode());
    }

    @Test
    public  void testInternalServerErrorResponse() throws IOException {
        DataciteResponseConverter converter = mock(DataciteResponseConverter.class);
        mock(DataciteResponseConverter.class);
        when(converter.toPublication(any(DataciteResponse.class), any(UUID.class), anyString()))
                .thenThrow(new RuntimeException("Fail"));
        Context context = getMockContext();
        MainHandler mainHandler = new MainHandler(objectMapper, converter, environment);
        OutputStream output = new ByteArrayOutputStream();

        mainHandler.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_INTERNAL_SERVER_ERROR, gatewayResponse.getStatusCode());
    }

    private Context getMockContext() {
        return mock(Context.class);
    }

    private InputStream inputStream() throws IOException {
        Map<String, Object> event = new HashMap<>();
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/datacite_response2.json")));
        event.put("requestContext",
                singletonMap("authorizer",
                        singletonMap("claims",
                                singletonMap("custom:feideId", "junit"))));
        event.put("body", body);
        event.put("headers", singletonMap(HttpHeaders.CONTENT_TYPE,
                ContentType.APPLICATION_JSON.getMimeType()));
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(event));
    }
}
