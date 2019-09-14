package pl.mendroch.modularization.core.graph;

import pl.mendroch.modularization.common.api.model.graph.Vertex;

import java.util.*;
import java.util.stream.Collectors;

public class GraphFlattener<T> {
    private final Map<Vertex<T>, List<Vertex<T>>> edges;
    private final Vertex<T> entry;
    private final List<Vertex<T>> flattened = new ArrayList<>();
    private final Map<Vertex, Boolean> visited = new HashMap<>();
    private final Map<Vertex, Boolean> recStack = new HashMap<>();

    public GraphFlattener(Map<Vertex<T>, List<Vertex<T>>> edges, Vertex<T> entry) {
        this.edges = edges;
        this.entry = entry;
    }

    List<T> flatten() {
        flatten(entry);
        return flattened.stream().map(Vertex::getValue).collect(Collectors.toList());
    }

    private boolean flatten(Vertex<T> vertex) {
        if (recStack.getOrDefault(vertex, false)) throw new IllegalStateException("Found cycle in a graph");
        if (visited.getOrDefault(vertex, false)) return false;

        visited.put(vertex, true);
        recStack.put(vertex, true);
        for (Vertex<T> edge : edges.getOrDefault(vertex, Collections.emptyList())) {
            if (flatten(edge))
                return true;
        }

        flattened.add(vertex);
        recStack.put(vertex, false);

        return false;
    }
}
