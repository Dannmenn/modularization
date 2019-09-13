package pl.mendroch.modularization.common.api.model.graph;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.emptyList;

public class Graph<K, V> {
    private final Map<Vertex<V>, List<Vertex<V>>> edges = new HashMap<>();
    private Map<V, K> mapper;

    public static <S, T> Builder<S, T> builder() {
        return new Builder<>();
    }

    private List<Vertex<V>> addVertexInternal(Vertex<V> vertex) {
        return edges.computeIfAbsent(vertex, v -> new ArrayList<>());
    }

    public Map<V, K> getMapper() {
        return mapper;
    }

    public Set<Vertex<V>> getVertices() {
        return edges.keySet();
    }

    public List<Vertex<V>> getEdges(Vertex<V> vertex) {
        return edges.getOrDefault(vertex, emptyList());
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

    public static class Builder<S, T> {
        private final Graph<S, T> graph = new Graph<>();

        public void dependencyMapper(Map<T, S> mapper) {
            graph.mapper = mapper;
        }

        public void addVertex(Vertex<T> vertex) {
            graph.addVertexInternal(vertex);
        }

        public void addEdge(Vertex<T> from, Vertex<T> to) {
            graph.addVertexInternal(from).add(to);
        }

        public Graph<S, T> createGraph() {
            return graph;
        }
    }
}
