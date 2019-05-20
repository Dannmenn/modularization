package pl.mendroch.modularization.common.api;

import pl.mendroch.modularization.common.api.annotation.TODO;
import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Graph.Builder;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.stream.Collectors.toMap;
import static pl.mendroch.modularization.common.api.model.graph.Vertex.vertexOf;

@TODO("Move to core")
public final class DependencyGraphUtils {
    private DependencyGraphUtils() {
        //Hide implicit constructor
    }

    public static Graph<ModuleJarInfo, Dependency> createDependencyGraph(Collection<ModuleJarInfo> modules, Map<Dependency, Dependency> overrides) {
        Builder<ModuleJarInfo, Dependency> builder = Graph.builder();
        Map<ModuleJarInfo, Dependency> mapper = new HashMap<>();
        for (ModuleJarInfo module : modules) {
            Dependency moduleDependency = mapper.computeIfAbsent(module, DependencyGraphUtils::convertModuleToDependency);
            Vertex<Dependency> moduleVertex = vertexOf(moduleDependency);
            builder.addVertex(moduleVertex);
            for (Dependency dependency : module.getDependencies()) {
                builder.addEdge(moduleVertex, vertexOf(overrides.getOrDefault(dependency, dependency)));
            }
        }
        builder.dependencyMapper(mapper.entrySet().stream().collect(toMap(Entry::getValue, Entry::getKey)));
        return builder.createGraph();
    }

    public static Dependency convertModuleToDependency(ModuleJarInfo module) {
        JarInfo jarInfo = module.getJarInfo();
        String name = jarInfo.getName();
        String[] parts = name.split(":");
        assert parts.length == 2 : "Invalid jar name format: " + name;
        return new Dependency(parts[0], parts[1], jarInfo.getSpecificationVersion());
    }
}
