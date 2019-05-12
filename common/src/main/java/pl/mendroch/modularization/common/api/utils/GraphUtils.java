package pl.mendroch.modularization.common.api.utils;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Vertex;

public final class GraphUtils {
    private GraphUtils() {
        //Hide implicit constructor
    }

    public static boolean isCyclic(Graph graph, Vertex entry) {
        //noinspection unchecked
        return new CycleDetector(graph, entry).isCyclic();
    }
}
