package pl.mendroch.modularization.core;

import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.tree.Node;
import pl.mendroch.modularization.common.api.model.tree.Root;
import pl.mendroch.modularization.core.model.EntryVertex;

import java.lang.module.ModuleDescriptor.Version;
import java.util.*;

import static pl.mendroch.modularization.common.api.utils.GraphUtils.isCyclic;

public class ApplicationGraph {
    private final Graph<ModuleJarInfo> graph;
    private final Root<ModuleJarInfo> root = new Root<>();
    private final Set<ModuleJarInfo> jars = new HashSet<>();
    private final Set<Vertex<ModuleJarInfo>> unused = new HashSet<>();
    private final Set<Vertex<ModuleJarInfo>> obsolete = new HashSet<>();
    private final EntryVertex entry = new EntryVertex();

    public ApplicationGraph(Graph<ModuleJarInfo> graph) {
        this.graph = graph;
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
        jars.addAll(nodes.keySet());
    }

    private void buildGraphNodes(Map<ModuleJarInfo, Node<ModuleJarInfo>> nodes, Node<ModuleJarInfo> root, Vertex<ModuleJarInfo> vertex) {
        for (Vertex<ModuleJarInfo> edge : graph.getEdges(vertex)) {
            ModuleJarInfo jarInfo = edge.getValue();
            boolean visited = nodes.containsKey(jarInfo);
            Node<ModuleJarInfo> node = nodes.computeIfAbsent(jarInfo, Node::new);
            root.addChildren(node);
            if (!visited) {
                buildGraphNodes(nodes, node, edge);
            }
        }
    }

    private void analyzeGraph() {
        Map<String, String> latestVersions = new HashMap<>();
        for (Vertex<ModuleJarInfo> vertex : graph.getVertices()) {
            ModuleJarInfo moduleJarInfo = vertex.getValue();
            if (!jars.contains(moduleJarInfo)) {
                unused.add(vertex);
            }
            JarInfo jarInfo = moduleJarInfo.getJarInfo();
            String version = latestVersions.computeIfAbsent(jarInfo.getName(), s -> jarInfo.getSpecificationVersion());
            if (jarIsObsolete(jarInfo, version)) {
                obsolete.add(vertex);
            }
        }
    }

    private boolean jarIsObsolete(JarInfo jarInfo, String latest) {
        String version = jarInfo.getSpecificationVersion();
        if (Objects.equals(version, latest)) {
            return false;
        }
        return Version.parse(version).compareTo(Version.parse(latest)) < 0;
    }

    private void initializeGraphWithEntryPoint() {
        graph.addVertex(entry);
        for (Vertex<ModuleJarInfo> vertex : graph.getVertices()) {
            String mainClass = vertex.getValue().getJarInfo().getMainClass();
            if (mainClass != null && !mainClass.isBlank()) {
                graph.addEdge(entry, vertex);
            }
        }
    }

    public Root<ModuleJarInfo> getRoot() {
        return root;
    }

    public Set<ModuleJarInfo> getJars() {
        return jars;
    }

    public Set<Vertex<ModuleJarInfo>> getUnused() {
        return unused;
    }

    public Set<Vertex<ModuleJarInfo>> getObsolete() {
        return obsolete;
    }
}
