package pl.mendroch.modularization.common.api.model.graph;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Vertex<T> {
    @EqualsAndHashCode.Include
    private final T value;
    private final int priority;
    private double factory = 1.;

    public static <T> Vertex<T> vertexOf(T value) {
        return new Vertex<>(value, 1);
    }

    public static <T> Vertex<T> vertexOf(T value, int priority) {
        return new Vertex<>(value, priority);
    }

    public int getPriority() {
        return (int) (factory * priority);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
