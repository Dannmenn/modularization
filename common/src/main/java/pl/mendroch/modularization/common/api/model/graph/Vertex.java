package pl.mendroch.modularization.common.api.model.graph;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class Vertex<V> {
    private final V value;

    public static <T> Vertex<T> vertexOf(T value) {
        return new Vertex<>(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
