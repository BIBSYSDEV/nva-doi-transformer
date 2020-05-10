package no.unit.nva.doi.transformer.utils;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SingletonCollector {

    public static final int SINGLETON = 1;
    public static final int ONLY_ELEMENT = 0;
    public static final String SINGLETON_EXPECTED_ERROR_TEMPLATE = "Expected a single value, but %d were found";
    public static final String SINGLETON_OR_NULL_EXPECTED_ERROR_TEMPLATE
            = "Expected zero or a single value, but %d were found";

    /**
     * A utility to collect and return singletons from lists.
     *
     * @param <T> the type of input elements to the reduction operation.
     * @return a singleton of type T.
     */
    public static <T> Collector<T, ?, T> collect() {
        return Collectors.collectingAndThen(Collectors.toList(), SingletonCollector::get);
    }

    private static <T> T get(List<T> list) {
        if (list.size() != SINGLETON) {
            throw new IllegalStateException(String.format(SINGLETON_EXPECTED_ERROR_TEMPLATE, list.size()));
        }
        return list.get(ONLY_ELEMENT);
    }

    public static <T> Collector<T, ?, T> collectOrElse(T alternative) {
        return Collectors.collectingAndThen(Collectors.toList(),  list -> orElse(list, alternative));
    }

    private static <T> T orElse(List<T> list, T alternative) {
        if (list.size() < SINGLETON) {
            return alternative;
        } else if (list.size() > SINGLETON) {
            throw new IllegalStateException(String.format(SINGLETON_OR_NULL_EXPECTED_ERROR_TEMPLATE, list.size()));
        }
        return list.get(ONLY_ELEMENT);
    }
}