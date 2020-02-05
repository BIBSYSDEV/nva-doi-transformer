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
import no.unit.nva.doi.transformer.model.internal.internal.Publication;
import org.zalando.problem.Problem;
import org.zalando.problem.ProblemModule;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
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

    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String BODY = "body";

    private final transient ObjectMapper objectMapper;
    private final transient DataciteResponseConverter converter;
    private final transient String allowedOrigin;

    public MainHandler() {
        this(createObjectMapper(), new DataciteResponseConverter(), new Environment());
    }

    /**
     * Constructor for MainHandler.
     *
     * @param objectMapper  objectMapper
     * @param converter converter
     * @param environment   environment
     */
    public MainHandler(ObjectMapper objectMapper, DataciteResponseConverter converter, Environment environment) {
        this.objectMapper = objectMapper;
        this.converter = converter;
        this.allowedOrigin = environment.get("ALLOWED_ORIGIN").orElseThrow(IllegalStateException::new);
    }

    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        DataciteResponse dataciteResponse;
        try {
            JsonNode event = objectMapper.readTree(input);
            System.out.println(objectMapper.writeValueAsString(event));
            String body = event.get(BODY).textValue();
            dataciteResponse = objectMapper.readValue(body, DataciteResponse.class);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(
                    Problem.valueOf(BAD_REQUEST, e.getMessage())), headers(), SC_BAD_REQUEST));
            return;
        }

        try {
            UUID uuid = UUID.randomUUID();
            String owner = context.getIdentity().getIdentityPoolId();
            Publication publication = converter.toPublication(dataciteResponse, uuid, owner);
            objectMapper.writeValue(output, new GatewayResponse<>(
                    objectMapper.writeValueAsString(publication), headers(), SC_OK));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            objectMapper.writeValue(output, new GatewayResponse<>(objectMapper.writeValueAsString(
                    Problem.valueOf(INTERNAL_SERVER_ERROR, e.getMessage())), headers(), SC_INTERNAL_SERVER_ERROR));
        }
    }

    private Map<String,String> headers() {
        Map<String,String> headers = new ConcurrentHashMap<>();
        headers.put(ACCESS_CONTROL_ALLOW_ORIGIN, allowedOrigin);
        headers.put(CONTENT_TYPE, APPLICATION_JSON.getMimeType());
        return headers;
    }

    /**
     * Create ObjectMapper.
     *
     * @return  objectMapper
     */
    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .registerModule(new ProblemModule())
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


}
