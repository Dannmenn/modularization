package pl.mendroch.modularization.common.api.utils;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CycleFlattener {
    private final Graph graph;
    private final Map<Dependency, ModuleJarInfo> mapper;
    private final Vertex entry;
    private final List<Vertex> vertices;
    private final boolean[] visited;
    private final boolean[] recStack;
    private final List<ModuleJarInfo> flattened = new ArrayList<>();

    public CycleFlattener(Graph graph, Vertex entry) {
        this.graph = graph;
        this.entry = entry;
        vertices = new ArrayList<>(graph.getVertices());
        mapper = graph.getMapper();
        visited = new boolean[vertices.size()];
        recStack = new boolean[vertices.size()];
    }

    public List<ModuleJarInfo> flatten() {
        flatten(vertices.indexOf(entry));
        return flattened;
    }

    private boolean flatten(int i) {
        if (recStack[i]) throw new IllegalStateException("Application graph cannot contain cyclic dependencies");
        if (visited[i]) return false;

        visited[i] = true;
        recStack[i] = true;
        Vertex vertex = vertices.get(i);
        for (Vertex edge : graph.getEdges(vertex)) {
            if (flatten(vertices.indexOf(edge)))
                return true;
        }

        flattened.add(getVertexValue(vertex));
        recStack[i] = false;

        return false;
    }

    private ModuleJarInfo getVertexValue(Vertex vertex) {
        return mapper.get(vertex.getValue());
    }
}
