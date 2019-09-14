package pl.mendroch.modularization.common.api.model.graph;

import lombok.extern.java.Log;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.util.*;
import java.util.Map.Entry;

import static java.util.Collections.emptySet;

@Log
public class Graph {
    private static final Comparator<Vertex<Dependency>> COMPARATOR = Comparator
            .<Vertex<Dependency>>comparingInt(Vertex::getPriority)
            .reversed()
            .thenComparing(Vertex::getValue);
    private final Map<Vertex<Dependency>, Set<Vertex<Dependency>>> edges = new HashMap<>();
    private Map<Dependency, ModuleJarInfo> mapper;

    public static Builder builder() {
        return new Builder();
    }

    private Set<Vertex<Dependency>> addVertexInternal(Vertex<Dependency> vertex) {
        return edges.computeIfAbsent(vertex, v -> new TreeSet<>(COMPARATOR));
    }

    public Map<Dependency, ModuleJarInfo> getMapper() {
        return mapper;
    }

    public Set<Vertex<Dependency>> getVertices() {
        return edges.keySet();
    }

    public Set<Vertex<Dependency>> getEdges(Vertex vertex) {
        return edges.getOrDefault(vertex, emptySet());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Entry<Vertex<Dependency>, Set<Vertex<Dependency>>> entry : edges.entrySet()) {
            builder
                    .append(entry.getKey())
                    .append("->")
                    .append(entry.getValue())
                    .append("\n");
        }
        return builder.toString();
    }

    public static class Builder {
        private final Graph graph = new Graph();

        public void dependencyMapper(Map<Dependency, ModuleJarInfo> mapper) {
            graph.mapper = mapper;
        }

        public void addVertex(Vertex<Dependency> vertex) {
            graph.addVertexInternal(vertex);
        }

        public void addEdge(Vertex<Dependency> from, Vertex<Dependency> to) {
            if (!graph.addVertexInternal(from).add(to)) {
                log.warning("Added Edge failed: " + to);
            }
            to.setFactory(to.getFactory() + 0.4);
        }

        public Graph createGraph() {
            return graph;
        }
    }
}
