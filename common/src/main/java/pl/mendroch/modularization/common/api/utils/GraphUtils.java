package pl.mendroch.modularization.common.api.utils;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.util.List;

public final class GraphUtils {
    private GraphUtils() {
        //Hide implicit constructor
    }

    public static List<ModuleJarInfo> flatten(Graph graph, Vertex entry) {
        return new GraphFlattener(graph, entry).flatten();
    }
}
