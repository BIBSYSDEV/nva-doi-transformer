package no.unit.nva.doi.transformer.language;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import no.unit.nva.doi.transformer.language.exceptions.LanguageUriNotFoundException;

public final class LanguageMapper {

    public static final String FIELD_DELIMITER = "\t";
    private static final Map<String, URI> ISO2URI = isoToURI(Path.of("languages", "lexvo-iso639-3.tsv"));
    public static final String ERROR_READING_FILE = "Could not read resource file:";
    public static final String URI_NOT_FOUND_ERROR = "Could not find a URI for the language:";

    private LanguageMapper() {
    }

    /**
     * Map an ISO639-3 language identifier to a Language URI.
     *
     * @param iso6393 An ISO639-3 identifier.
     * @return a language URI if this mapping is available or an empty {@link Optional} if there is no such mapping.
     */
    public static Optional<URI> getUriOpt(String iso6393) {
        if(iso6393!=null) {
            return Optional.ofNullable(ISO2URI.get(iso6393));
        }
        else
            return Optional.empty();
    }

    /**
     * Map an ISO639-3 language identifier to a Language URI.
     *
     * @param iso6393 An ISO639-3 identifier.
     * @return a language URI if this mapping is available or an empty {@link Optional} if there is no such mapping.
     * @throws LanguageUriNotFoundException when there is no mapping between the input string the available mappings.
     */
    public static URI getURI(String iso6393) {
        if (!ISO2URI.containsKey(iso6393)) {
            throw new LanguageUriNotFoundException(URI_NOT_FOUND_ERROR + iso6393);
        }
        return ISO2URI.get(iso6393);
    }

    public static Collection<URI> languageUris() {
        return ISO2URI.values();
    }

    // For some reason it does not like the mapping to SimpleEntry
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private static Map<String, URI> isoToURI(Path path) {

        List<String> lines = linesfromResource(path);
        Map<String, URI> isoToUri = lines
            .stream()
            .map(line -> line.split(FIELD_DELIMITER))
            .filter(array -> array.length >= 2)
            .map(array -> new ConcurrentHashMap.SimpleEntry<>(array[0], array[1]))
            .map(e -> new ConcurrentHashMap.SimpleEntry<>(e.getKey(), URI.create(e.getValue())))
            .collect(Collectors.toConcurrentMap(SimpleEntry::getKey, SimpleEntry::getValue));
        return Collections.unmodifiableMap(isoToUri);
    }

    private static InputStream inputStreamFromResources(Path path) {
        String pathString = path.toString();
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(pathString);
    }

    private static InputStreamReader newInputStreamReader(InputStream stream) {
        return new InputStreamReader(stream, StandardCharsets.UTF_8);
    }

    private static List<String> linesfromResource(Path path) {
        try (BufferedReader reader = new BufferedReader(newInputStreamReader(inputStreamFromResources(path)))) {
            List<String> lines = new ArrayList<>();
            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
            return lines;
        } catch (Exception e) {
            throw new RuntimeException(ERROR_READING_FILE + path.toString());
        }
    }
}
