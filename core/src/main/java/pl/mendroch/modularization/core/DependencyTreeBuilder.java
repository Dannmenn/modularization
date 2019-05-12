package pl.mendroch.modularization.core;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.tree.Node;
import pl.mendroch.modularization.common.api.model.tree.Root;
import pl.mendroch.modularization.core.model.EntryVertex;

import java.lang.module.ModuleDescriptor.Version;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static pl.mendroch.modularization.common.api.utils.GraphUtils.isCyclic;

public class DependencyTreeBuilder {
    private final Graph<ModuleJarInfo, Dependency> graph;
    private final Map<Dependency, ModuleJarInfo> mapper;
    private final Root<ModuleJarInfo> root = new Root<>();
    private final Set<Dependency> jars = new HashSet<>();
    private final Set<JarInfo> unused = new HashSet<>();
    private final Set<JarInfo> obsolete = new HashSet<>();
    private final EntryVertex entry = new EntryVertex();

    public DependencyTreeBuilder(Graph<ModuleJarInfo, Dependency> graph) {
        this.graph = graph;
        mapper = graph.getMapper();
        initializeGraphWithEntryPoint();
        validateGraph();
        buildGraph();
        analyzeGraph();
    }

    private void validateGraph() {
        if (isCyclic(graph, entry)) {
            throw new IllegalStateException("Application graph cannot contain cyclic dependencies");
        }
    }

    private void buildGraph() {
        Map<ModuleJarInfo, Node<ModuleJarInfo>> nodes = new HashMap<>();
        buildGraphNodes(nodes, root, entry);
    }

    private void buildGraphNodes(Map<ModuleJarInfo, Node<ModuleJarInfo>> nodes, Node<ModuleJarInfo> root, Vertex<Dependency> vertex) {
        for (Vertex<Dependency> edge : graph.getEdges(vertex)) {
            Dependency dependency = edge.getValue();
            ModuleJarInfo jarInfo = mapper.get(dependency);
            boolean visited = nodes.containsKey(jarInfo);
            jars.add(dependency);
            Node<ModuleJarInfo> node = nodes.computeIfAbsent(jarInfo, Node::new);
            root.addChildren(node);
            if (!visited) {
                buildGraphNodes(nodes, node, edge);
            }
        }
    }

    private void analyzeGraph() {
        Map<String, Version> latestVersions = new HashMap<>();
        for (Vertex<Dependency> vertex : graph.getVertices()) {
            Dependency dependency = vertex.getValue();
            JarInfo jarInfo = mapper.get(dependency).getJarInfo();
            if (!jars.contains(dependency)) {
                unused.add(jarInfo);
            }
            Version jarVersion = Version.parse(jarInfo.getSpecificationVersion());
            Version latest = latestVersions.computeIfAbsent(jarInfo.getName(), s -> jarVersion);
            if (!jarIsObsolete(latest, jarVersion)) {
                latestVersions.put(jarInfo.getName(), jarVersion);
            }
        }
        for (Vertex<Dependency> vertex : graph.getVertices()) {
            Dependency dependency = vertex.getValue();
            JarInfo jarInfo = mapper.get(dependency).getJarInfo();
            Version jarVersion = Version.parse(jarInfo.getSpecificationVersion());
            Version latest = latestVersions.get(jarInfo.getName());
            if (jarIsObsolete(latest, jarVersion)) {
                obsolete.add(jarInfo);
            }
        }
    }

    private boolean jarIsObsolete(Version latest, Version version) {
        return version.compareTo(latest) < 0;
    }

    private void initializeGraphWithEntryPoint() {
        graph.addVertex(entry);
        for (Vertex<Dependency> vertex : graph.getVertices()) {
            String mainClass = mapper.get(vertex.getValue()).getJarInfo().getMainClass();
            if (mainClass != null && !mainClass.isBlank()) {
                graph.addEdge(entry, vertex);
            }
        }
    }

    public Graph<ModuleJarInfo, Dependency> getGraph() {
        return graph;
    }

    public Root<ModuleJarInfo> getRoot() {
        return root;
    }

    public Set<Dependency> getJars() {
        return jars;
    }

    public Set<JarInfo> getUnused() {
        return unused;
    }

    public Set<JarInfo> getObsolete() {
        return obsolete;
    }
}
