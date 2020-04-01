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
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import no.unit.nva.doi.transformer.language.exceptions.LanguageUriNotFoundException;

public final class LanguageMapper {

    public static final String FIELD_DELIMITER = "\t";
    public static final Path LANGUAGE_URIS_RESOURCE = Path.of("languages", "lexvo-iso639-3.tsv");
    private static final Map<String, URI> ISO2URI = isoToUri(LANGUAGE_URIS_RESOURCE);
    private static final Map<URI, String> URI2ISO = uriToIso(LANGUAGE_URIS_RESOURCE);
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
    public static Optional<URI> getUriFromIso639AsOptional(String iso6393) {
        if (iso6393 != null) {
            return Optional.ofNullable(ISO2URI.get(iso6393));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns an ISO639-3 code for the specified URI.
     *
     * @param uri A language URI
     * @return An ISO639-3 code or empty optional if there is no mapping for the input URI
     */
    public static Optional<String> getIsoAsOptional(URI uri) {
        if (uri != null) {
            return Optional.ofNullable(URI2ISO.get(uri));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Map an ISO639-3 language identifier to a Language URI.
     *
     * @param iso6393 An ISO639-3 identifier.
     * @return a language URI if this mapping is available or an empty {@link Optional} if there is no such mapping.
     * @throws LanguageUriNotFoundException when there is no mapping between the input string the available mappings.
     */
    public static URI getUriFromIso639(String iso6393) throws LanguageUriNotFoundException {
        if (!ISO2URI.containsKey(iso6393)) {
            throw new LanguageUriNotFoundException(URI_NOT_FOUND_ERROR + iso6393);
        }
        return ISO2URI.get(iso6393);
    }

    /**
     * Map an Language URI to an ISO639-3 string.
     *
     * @param langUri A language URI from https://www.lexvo.org/.
     * @return an ISO639-3 language code
     * @throws LanguageUriNotFoundException when there is no mapping for the specified URI
     */
    public static URI getIso(URI langUri) throws LanguageUriNotFoundException {
        if (!URI2ISO.containsKey(langUri)) {
            throw new LanguageUriNotFoundException(URI_NOT_FOUND_ERROR + langUri);
        }
        return ISO2URI.get(langUri);
    }

    public static Collection<URI> languageUris() {
        return ISO2URI.values();
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

    // For some reason it does not like the mapping to SimpleEntry
    @SuppressWarnings("PMD.UseConcurrentHashMap")
    private static Map<URI, String> uriToIso(Path path) {

        List<String> lines = linesfromResource(path);
        ConcurrentMap<URI, String> uriToIso = lines
            .stream()
            .map(line -> splitLineToArray(line))
            .filter(array -> array.length >= 2)
            .map(array -> new SimpleEntry<>(array[1], array[0]))
            .map(e -> new SimpleEntry<>(URI.create(e.getKey()), e.getValue()))
            .collect(Collectors.toConcurrentMap(SimpleEntry::getKey, SimpleEntry::getValue));
        return Collections.unmodifiableMap(uriToIso);
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
