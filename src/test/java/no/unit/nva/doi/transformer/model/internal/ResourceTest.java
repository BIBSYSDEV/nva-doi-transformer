package no.unit.nva.doi.transformer.model.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.unit.nva.doi.transformer.MainHandler;
import no.unit.nva.doi.transformer.model.internal.internal.Creator;
import no.unit.nva.doi.transformer.model.internal.internal.FileMetadata;
import no.unit.nva.doi.transformer.model.internal.internal.Metadata;
import no.unit.nva.doi.transformer.model.internal.internal.Resource;
import org.junit.Test;

import java.time.Instant;
import java.util.Collections;

public class ResourceTest {

    private ObjectMapper objectMapper = MainHandler.createObjectMapper();

    @Test
    public void test() throws JsonProcessingException {

        Resource resource = new Resource.Builder()
                .withResourceIdentifier("resourceIdentifier")
                .withCreatedDate(Instant.now().toString())
                .withFiles(Collections.singletonMap("fileIdentifier", new FileMetadata.Builder()
                        .withChecksum("123")
                        .withFilename("some_file.pdf")
                        .withMimeType("application/pdf")
                        .withSize("42")
                        .build())
                )
                .withIndexedDate(Instant.now().toString())
                .withMetadata(new Metadata.Builder()
                        .withCreators(Collections.singletonList(
                                new Creator.Builder()
                                .build()
                        ))
                        .withHandle("handle")
                        .withLicenseIdentifier("licenseIdentifier")
                        .withPublicationYear("2020")
                        .withPublisher("publisher")
                        .withResourceType("resourceType")
                        .withTitles(Collections.singletonMap("no", "Norsk tittel"))
                        .build())
                .withModifiedDate(Instant.now().toString())
                .withOwner("owner")
                .withPublishedDate(Instant.now().toString())
                .withStatus("draft")
                .build();

        String json = objectMapper.writeValueAsString(resource);
        System.out.println(json);
    }

}
