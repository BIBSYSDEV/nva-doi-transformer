package no.unit.nva.doi.transformer.model;

import com.amazonaws.services.lambda.runtime.CognitoIdentity;
import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.doi.transformer.DataciteResponseConverter;
import no.unit.nva.doi.transformer.GatewayResponse;
import no.unit.nva.doi.transformer.MainHandler;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.doi.transformer.model.internal.internal.Resource;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    @Test
    public void testDefaultConstructor() {
        MainHandler mainHandler = new MainHandler();
        assertNotNull(mainHandler);
    }

    @Test
    public void testOkResponse() throws IOException {
        DataciteResponseConverter converter = new DataciteResponseConverter();
        Context context = getMockContext();
        MainHandler mainHandler = new MainHandler(objectMapper, converter);
        OutputStream output = new ByteArrayOutputStream();

        mainHandler.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_OK, gatewayResponse.getStatusCode());
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains(CONTENT_TYPE));
        Assert.assertTrue(gatewayResponse.getHeaders().keySet().contains(ACCESS_CONTROL_ALLOW_ORIGIN));
        Resource resource = objectMapper.readValue(gatewayResponse.getBody().toString(),
                Resource.class);
        assertEquals(DataciteResponseConverter.DEFAULT_NEW_RESOURCE_STATUS, resource.getStatus());

    }

    @Test
    public void testBadRequestresponse() throws IOException {
        DataciteResponseConverter converter = new DataciteResponseConverter();
        Context context = getMockContext();
        MainHandler mainHandler = new MainHandler(objectMapper, converter);
        OutputStream output = new ByteArrayOutputStream();

        mainHandler.handleRequest(new ByteArrayInputStream(new byte[0]), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_BAD_REQUEST, gatewayResponse.getStatusCode());
    }

    @Test
    public  void testInternalServerErrorResponse() throws IOException {
        DataciteResponseConverter converter = mock(DataciteResponseConverter.class);
        mock(DataciteResponseConverter.class);
        when(converter.toResource(any(DataciteResponse.class), anyString(), anyString()))
                .thenThrow(new RuntimeException("Fail"));
        Context context = getMockContext();
        MainHandler mainHandler = new MainHandler(objectMapper, converter);
        OutputStream output = new ByteArrayOutputStream();

        mainHandler.handleRequest(inputStream(), output, context);

        GatewayResponse gatewayResponse = objectMapper.readValue(output.toString(), GatewayResponse.class);
        assertEquals(SC_INTERNAL_SERVER_ERROR, gatewayResponse.getStatusCode());
    }

    private Context getMockContext() {
        Context context = mock(Context.class);
        CognitoIdentity cognitoIdentity = mock(CognitoIdentity.class);
        when(context.getIdentity()).thenReturn(cognitoIdentity);
        when(cognitoIdentity.getIdentityPoolId()).thenReturn("junit");
        return context;
    }

    private InputStream inputStream() throws IOException {
        Map<String, Object> event = new HashMap<>();
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/datacite_response.json")));
        event.put("body", body);
        event.put("headers", Collections.singletonMap(HttpHeaders.CONTENT_TYPE,
                ContentType.APPLICATION_JSON.getMimeType()));
        return new ByteArrayInputStream(objectMapper.writeValueAsBytes(event));
    }
}
