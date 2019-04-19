package pl.mendroch.modularization.common.api;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Graph.Builder;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.util.Collection;

import static pl.mendroch.modularization.common.api.model.graph.Vertex.vertexOf;

public final class DependencyGraphUtils {
    private DependencyGraphUtils() {
    }

    public static Graph<Dependency> createDependencyGraph(Collection<ModuleJarInfo> modules) {
        Builder<Dependency> builder = Graph.builder();
        for (ModuleJarInfo module : modules) {
            Vertex<Dependency> moduleVertex = vertexOf(convertModuleToDependency(module));
            builder.addVertex(moduleVertex);
            for (Dependency dependency : module.getDependencies()) {
                builder.addEdge(moduleVertex, vertexOf(dependency));
            }
        }
        return builder.createGraph();
    }

    private static Dependency convertModuleToDependency(ModuleJarInfo module) {
        JarInfo jarInfo = module.getJarInfo();
        String name = jarInfo.getName();
        String[] parts = name.split(":");
        assert parts.length == 2 : "Invalid jar name format: " + name;
        return new Dependency(parts[0], parts[1], jarInfo.getSpecificationVersion());
    }
}
