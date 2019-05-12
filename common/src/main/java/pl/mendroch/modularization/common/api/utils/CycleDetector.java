package pl.mendroch.modularization.common.api.utils;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Vertex;

import java.util.ArrayList;
import java.util.List;

class CycleDetector<V> {
    private final Graph<?, V> graph;
    private final Vertex<V> entry;
    private final List<Vertex<V>> vertices;
    private final boolean[] visited;
    private final boolean[] recStack;

    CycleDetector(Graph<?, V> graph, Vertex<V> entry) {
        this.graph = graph;
        this.entry = entry;
        vertices = new ArrayList<>(graph.getVertices());
        visited = new boolean[vertices.size()];
        recStack = new boolean[vertices.size()];
    }

    boolean isCyclic() {
        return hasCycle(vertices.indexOf(entry));
    }

    private boolean hasCycle(int i) {
        if (recStack[i])
            return true;

        if (visited[i])
            return false;

        visited[i] = true;
        recStack[i] = true;
        for (Vertex<V> edge : graph.getEdges(vertices.get(i))) {
            if (hasCycle(vertices.indexOf(edge)))
                return true;
        }

        recStack[i] = false;

        return false;
    }
}
