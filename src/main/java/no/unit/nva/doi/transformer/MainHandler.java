package no.unit.nva.doi.transformer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import no.unit.nva.doi.transformer.model.internal.external.DataciteResponse;
import no.unit.nva.model.Publication;
import no.unit.nva.model.util.OrgNumberMapper;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.zalando.problem.Status.BAD_REQUEST;
import static org.zalando.problem.Status.INTERNAL_SERVER_ERROR;

public class MainHandler implements RequestStreamHandler {

    public static final String ORG_NUMBER_COUNTRY_PREFIX_NORWAY = "NO";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String BODY = "body";

    public static final String REQUEST_CONTEXT_AUTHORIZER_CLAIMS = "/requestContext/authorizer/claims/";
    public static final String CUSTOM_FEIDE_ID = "custom:feideId";
    public static final String CUSTOM_ORG_NUMBER = "custom:orgNumber";
    public static final String MISSING_CLAIM_IN_REQUEST_CONTEXT =
            "Missing claim in requestContext: ";

    public static final String ALLOWED_ORIGIN = "ALLOWED_ORIGIN";
    public static final String ENVIRONMENT_VARIABLE_NOT_SET = "Environment variable not set: ";

    private final transient ObjectMapper objectMapper;
    private final transient DataciteResponseConverter converter;
    private final transient String allowedOrigin;

    public MainHandler() {
        this(createObjectMapper(), new DataciteResponseConverter(), new Environment());
    }

    /**
     * Constructor for MainHandler.
     *
     * @param objectMapper objectMapper
     * @param converter    converter
     * @param environment  environment
     */
    public MainHandler(ObjectMapper objectMapper, DataciteResponseConverter converter, Environment environment) {
        this.objectMapper = objectMapper;
        this.converter = converter;
        this.allowedOrigin = environment.get(ALLOWED_ORIGIN)
                .orElseThrow(() -> new IllegalStateException(ENVIRONMENT_VARIABLE_NOT_SET + ALLOWED_ORIGIN));
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        DataciteResponse dataciteResponse;
        String owner;
        String orgNumber;
        try {
            JsonNode event = objectMapper.readTree(input);
            String body = event.get(BODY).textValue();
            dataciteResponse = objectMapper.readValue(body, DataciteResponse.class);
            owner = getClaimValueFromRequestContext(event, CUSTOM_FEIDE_ID);
            orgNumber = getClaimValueFromRequestContext(event, CUSTOM_ORG_NUMBER);
        } catch (Exception e) {
            e.printStackTrace();
            objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(
                    Problem.valueOf(BAD_REQUEST, e.getMessage())), headers(), SC_BAD_REQUEST));
            return;
        }

        try {
            UUID uuid = UUID.randomUUID();
            URI publisherID = toPublisherId(orgNumber);
            Publication publication = converter.toPublication(dataciteResponse, uuid, owner, publisherID);
            log(objectMapper.writeValueAsString(publication));
            objectMapper.writeValue(output, new GatewayResponse<>(
                    objectMapper.writeValueAsString(publication), headers(), SC_OK));
        } catch (Exception e) {
            e.printStackTrace();
            objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(
                    Problem.valueOf(INTERNAL_SERVER_ERROR, e.getMessage())), headers(), SC_INTERNAL_SERVER_ERROR));
        }
    }

    private Map<String, String> headers() {
        Map<String, String> headers = new ConcurrentHashMap<>();
        headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
        headers.put(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        return headers;
    }

    /**
     * Create ObjectMapper.
     *
     * @return objectMapper
     */
    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .registerModule(new ProblemModule())
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

    public static void log(String message) {
        System.out.println(message);
    }

    private URI toPublisherId(String orgNumber) {
        if (orgNumber.startsWith(ORG_NUMBER_COUNTRY_PREFIX_NORWAY)) {
            // Remove this if and when datamodel has support for OrgNumber country prefix
            return OrgNumberMapper.toCristinId(orgNumber.substring(ORG_NUMBER_COUNTRY_PREFIX_NORWAY.length()));
        }
        return OrgNumberMapper.toCristinId(orgNumber);
    }

    private String getClaimValueFromRequestContext(JsonNode event, String claimName) {
        return Optional.ofNullable(event.at(REQUEST_CONTEXT_AUTHORIZER_CLAIMS + claimName).textValue())
                .orElseThrow(() -> new IllegalArgumentException(MISSING_CLAIM_IN_REQUEST_CONTEXT + claimName));
    }

}
