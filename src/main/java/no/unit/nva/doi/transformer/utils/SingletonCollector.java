package no.unit.nva.doi.transformer.utils;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SingletonCollector {

    public static final int SINGLETON = 1;
    public static final int ONLY_ELEMENT = 0;

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
            throw new IllegalStateException();
        }
        return list.get(ONLY_ELEMENT);
    }
}
