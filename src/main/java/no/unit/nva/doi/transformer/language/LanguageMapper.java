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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import no.unit.nva.doi.transformer.language.exceptions.LanguageUriNotFoundException;

public final class LanguageMapper {

    public static final String FIELD_DELIMITER = "\t";
    public static final Path LANGUAGE_URIS_RESOURCE = Path.of("languages", "lexvo-iso639-3.tsv");
    private static final Map<String, URI> ISO2URI = isoToUri(LANGUAGE_URIS_RESOURCE);
    public static final String ERROR_READING_FILE = "Could not read resource file:";
    public static final String URI_NOT_FOUND_ERROR = "Could not find a URI for the language:";

    private LanguageMapper() {
    }

    /**
     * Map an ISO639-3 language identifier to a Language URI.
     *
     * @param iso An ISO639-3 identifier.
     * @return a language URI if this mapping is available or an empty {@link Optional} if there is no such mapping.
     */
    public static Optional<URI> getUriFromIsoAsOptional(String iso) {
        if (iso != null) {
            return Optional.ofNullable(ISO2URI.get(iso));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Map an ISO639-3 language identifier to a Language URI.
     *
     * @param iso An ISO639-3 identifier.
     * @return a language URI if this mapping is available or an empty {@link Optional} if there is no such mapping.
     * @throws LanguageUriNotFoundException when there is no mapping between the input string the available mappings.
     */
    public static URI getUriFromIso(String iso) throws LanguageUriNotFoundException {
        if (!ISO2URI.containsKey(iso)) {
            throw new LanguageUriNotFoundException(URI_NOT_FOUND_ERROR + iso);
        }
        return ISO2URI.get(iso);
    }

    public static Collection<URI> languageUris() {
        return new HashSet<>(ISO2URI.values());
    }

    // For some reason it does not like the mapping to SimpleEntry
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private static Map<String, URI> isoToUri(Path path) {

        List<String> lines = linesfromResource(path);
        Map<String, URI> isoToUri = lines
            .stream()
            .map(LanguageMapper::splitLineToArray)
            .filter(LanguageMapper::keepOnlyLinesWithTwoEntries)
            .map(array -> createMapEntry(array[0], array[1]))
            .map(e -> createMapEntry(e.getKey(), URI.create(e.getValue())))
            .collect(Collectors.toConcurrentMap(SimpleEntry::getKey, SimpleEntry::getValue));
        return Collections.unmodifiableMap(isoToUri);
    }

    private static <K, V> SimpleEntry<K, V> createMapEntry(K key, V value) {
        return new ConcurrentHashMap.SimpleEntry<>(key, value);
    }

    private static boolean keepOnlyLinesWithTwoEntries(String[] array) {
        return array.length >= 2;
    }

    private static String[] splitLineToArray(String line) {
        return line.split(FIELD_DELIMITER);
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
