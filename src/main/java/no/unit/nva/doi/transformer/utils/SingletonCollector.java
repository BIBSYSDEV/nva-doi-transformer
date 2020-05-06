package no.unit.nva.doi.transformer.utils;

import java.util.stream.Collector;
import java.util.stream.Collectors;

public class SingletonCollector {
    public static <T> Collector<T, ?, T> collect() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.get(0);
                }
        );
    }
}
