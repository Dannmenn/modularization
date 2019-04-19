package pl.mendroch.modularization.common.api.model.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Graph<V> {
    private final Map<Vertex<V>, List<Vertex<V>>> edges = new HashMap<>();

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public void addVertex(Vertex<V> vertex) {
        addVertexInternal(vertex);
    }

    public void addEdge(Vertex<V> from, Vertex<V> to) {
        addVertexInternal(from).add(to);
    }

    private List<Vertex<V>> addVertexInternal(Vertex<V> vertex) {
        return edges.computeIfAbsent(vertex, v -> new ArrayList<>());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<Vertex<V>, List<Vertex<V>>> entry : edges.entrySet()) {
            builder
                    .append(entry.getKey())
                    .append("->")
                    .append(entry.getValue())
                    .append("\n");
        }
        return builder.toString();
    }

    public static class Builder<T> {
        private final Graph<T> graph = new Graph<>();

        public Builder addVertex(Vertex<T> vertex) {
            graph.addVertexInternal(vertex);
            return this;
        }

        public Builder addEdge(Vertex<T> from, Vertex<T> to) {
            graph.addVertexInternal(from).add(to);
            return this;
        }

        public Graph<T> createGraph() {
            return graph;
        }
    }
}
