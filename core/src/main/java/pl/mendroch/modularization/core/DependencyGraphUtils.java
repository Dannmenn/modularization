package pl.mendroch.modularization.core;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Graph.Builder;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Requires;
import java.lang.module.ModuleDescriptor.Version;
import java.util.*;
import java.util.Map.Entry;

import static java.lang.module.ModuleDescriptor.Requires.Modifier.STATIC;
import static java.util.stream.Collectors.toMap;
import static pl.mendroch.modularization.common.api.model.graph.Vertex.vertexOf;
import static pl.mendroch.modularization.common.api.model.modules.Dependency.UNSPECIFIED;

public final class DependencyGraphUtils {
    private DependencyGraphUtils() {
        //Hide implicit constructor
    }

    public static Graph<ModuleJarInfo, Dependency> createDependencyGraph(Collection<ModuleJarInfo> modules, Map<Dependency, Dependency> overrides) {
        Builder<ModuleJarInfo, Dependency> builder = Graph.builder();
        Map<ModuleJarInfo, Dependency> mapper = new HashMap<>();
        Map<Vertex<Dependency>, List<Dependency>> dependencies = new HashMap<>();
        Map<String, Map<String, Vertex<Dependency>>> dependencyMapper = new HashMap<>();
        Map<String, List<Vertex<Dependency>>> uses = new HashMap<>();
        Map<String, List<Vertex<Dependency>>> provides = new HashMap<>();
        for (ModuleJarInfo module : modules) {
            ModuleDescriptor descriptor = module.getDescriptor();
            Dependency moduleDependency = getModuleAsDependency(overrides, mapper, module);
            if (moduleDependency == null) continue;
            Vertex<Dependency> moduleVertex = vertexOf(moduleDependency);
            addVertexMappings(dependencyMapper, descriptor, moduleDependency, moduleVertex);
            descriptor.uses().forEach(s -> uses.computeIfAbsent(s, s1 -> new ArrayList<>()).add(moduleVertex));
            descriptor.provides().forEach(s -> provides.computeIfAbsent(s.service(), s1 -> new ArrayList<>()).add(moduleVertex));
            builder.addVertex(moduleVertex);
            Properties versions = module.getDependencyVersions();
            List<Dependency> moduleDependencies = dependencies.computeIfAbsent(moduleVertex, dependencyVertex -> new ArrayList<>());
            descriptor
                    .requires().stream()
                    .filter(req -> !req.name().startsWith("jdk"))
                    .filter(req -> !req.name().startsWith("java"))
                    .filter(req -> !req.name().startsWith("pl.mendroch.modularization"))
                    .filter(req -> !req.modifiers().contains(STATIC))
                    .map(Requires::name)
                    .forEach(name -> {
                        String version = Optional.ofNullable((String) versions.get(name)).orElse(UNSPECIFIED);
                        Dependency dep = new Dependency(name, version);
                        moduleDependencies.add(overrides.getOrDefault(dep, dep));
                    });
        }

        createEdgesForDependencies(builder, dependencies, dependencyMapper);
        createDependenciesBasedOnServices(builder, uses, provides);

        builder.dependencyMapper(mapper.entrySet().stream().collect(toMap(Entry::getValue, Entry::getKey)));
        return builder.createGraph();
    }

    private static void createDependenciesBasedOnServices(Builder<ModuleJarInfo, Dependency> builder, Map<String, List<Vertex<Dependency>>> uses, Map<String, List<Vertex<Dependency>>> provides) {
        uses.forEach((use, vertices) -> {
            List<Vertex<Dependency>> providers = provides.get(use);
            if (!providers.isEmpty()) {
                for (Vertex<Dependency> vertex : vertices) {
                    providers.forEach(provider -> {
                        if (!vertex.equals(provider))
                            builder.addEdge(vertex, provider);
                    });
                }
            }
        });
    }

    private static void createEdgesForDependencies(Builder<ModuleJarInfo, Dependency> builder, Map<Vertex<Dependency>, List<Dependency>> dependencies, Map<String, Map<String, Vertex<Dependency>>> dependencyMapper) {
        dependencies.forEach((vertex, moduleDependencies) -> {
            for (Dependency dependency : moduleDependencies) {
                Map<String, Vertex<Dependency>> vertexMap = dependencyMapper.get(dependency.getName());
                if (vertexMap == null) continue;
                Vertex<Dependency> depVertex = vertexMap.getOrDefault(dependency.getVersion(), vertexMap.get(UNSPECIFIED));
                builder.addEdge(vertex, depVertex);
            }
        });
    }

    private static void addVertexMappings(Map<String, Map<String, Vertex<Dependency>>> dependencyMapper, ModuleDescriptor descriptor, Dependency moduleDependency, Vertex<Dependency> moduleVertex) {
        Map<String, Vertex<Dependency>> vertexMap = dependencyMapper.computeIfAbsent(descriptor.name(), s -> new HashMap<>());
        vertexMap.put(moduleDependency.getVersion(), moduleVertex);
        vertexMap.merge(UNSPECIFIED, moduleVertex, (left, right) -> {
            if (Version.parse(left.getValue().getVersion()).compareTo(
                    Version.parse(right.getValue().getVersion())) > 0) {
                return left;
            }
            return right;
        });
    }

    private static Dependency getModuleAsDependency(Map<Dependency, Dependency> overrides, Map<ModuleJarInfo, Dependency> mapper, ModuleJarInfo module) {
        Dependency moduleDependency = mapper.computeIfAbsent(module, DependencyGraphUtils::convertModuleToDependency);
        if (overrides.containsKey(moduleDependency)) {
            return null;
        }
        return moduleDependency;
    }

    public static Dependency convertModuleToDependency(ModuleJarInfo module) {
        JarInfo jarInfo = module.getJarInfo();
        ModuleDescriptor descriptor = module.getDescriptor();
        return new Dependency(descriptor.name(), jarInfo.getSpecificationVersion());
    }
}
