package pl.mendroch.modularization.common.api.model.graph;

import java.util.Objects;

public class Vertex<V> {
    private final V value;

    public Vertex(V value) {
        this.value = value;
    }

    public static <T> Vertex<T> vertexOf(T value) {
        return new Vertex<>(value);
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex<?> vertex = (Vertex<?>) o;
        return value.equals(vertex.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
