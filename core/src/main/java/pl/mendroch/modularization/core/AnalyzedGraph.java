package pl.mendroch.modularization.core;

import lombok.Getter;
import pl.mendroch.modularization.common.api.model.graph.Graph;
import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.Dependency;
import pl.mendroch.modularization.common.api.model.modules.JarInfo;
import pl.mendroch.modularization.common.api.model.modules.ModuleJarInfo;
import pl.mendroch.modularization.common.api.model.modules.OptionalDependency;
import pl.mendroch.modularization.common.api.model.tree.Node;
import pl.mendroch.modularization.common.api.utils.GraphUtils;

import java.lang.module.ModuleDescriptor.Version;
import java.util.*;

public class AnalyzedGraph {
    @Getter
    private final Graph graph;
    private final Map<Dependency, ModuleJarInfo> mapper;
    @Getter
    private final Set<Dependency> jars = new HashSet<>();
    @Getter
    private final Set<ModuleJarInfo> thirdPartyJars = new HashSet<>();
    @Getter
    private final Set<JarInfo> obsolete = new HashSet<>();
    @Getter
    private Node<ModuleJarInfo> root;
    private Vertex<Dependency> entry;
    @Getter
    private List<ModuleJarInfo> flattened;

    public AnalyzedGraph(Graph graph) {
        this.graph = graph;
        mapper = graph.getMapper();
        initializeGraphWithEntryPoint();
        flattened = GraphUtils.flatten(graph, entry);
        buildGraph();
        analyzeGraph();
    }

    private void buildGraph() {
        Map<ModuleJarInfo, Node<ModuleJarInfo>> nodes = new HashMap<>();
        root = new Node<>(mapper.get(entry.getValue()));
        jars.add(entry.getValue());
        buildGraphNodes(nodes, root, entry);
    }

    private void buildGraphNodes(Map<ModuleJarInfo, Node<ModuleJarInfo>> nodes, Node<ModuleJarInfo> root, Vertex<Dependency> vertex) {
        for (Vertex<Dependency> edge : graph.getEdges(vertex)) {
            Dependency dependency = edge.getValue();
            ModuleJarInfo jarInfo = mapper.get(dependency);
            if (jarInfo == null || dependency instanceof OptionalDependency) {
                continue;
            }
            boolean visited = nodes.containsKey(jarInfo);
            jars.add(dependency);
            Node<ModuleJarInfo> node = nodes.computeIfAbsent(jarInfo, Node::new);
            root.addChild(node);
            if (!visited) {
                buildGraphNodes(nodes, node, edge);
            }
        }
    }

    private void analyzeGraph() {
        String skipThirdPartyGroups = System.getProperty("skip.third.party.group");
        if (skipThirdPartyGroups == null) skipThirdPartyGroups = "";
        Map<String, Version> latestVersions = new HashMap<>();
        for (Vertex<Dependency> vertex : graph.getVertices()) {
            Dependency dependency = vertex.getValue();
            JarInfo jarInfo = mapper.get(dependency).getJarInfo();
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
            } else if (!jars.contains(dependency) && !skipThirdPartyGroups.startsWith(dependency.getName())) {
                thirdPartyJars.add(mapper.get(dependency));
            }
        }
    }

    private boolean jarIsObsolete(Version latest, Version version) {
        return version.compareTo(latest) < 0;
    }

    private void initializeGraphWithEntryPoint() {
        for (Vertex<Dependency> vertex : graph.getVertices()) {
            String mainClass = mapper.get(vertex.getValue()).getJarInfo().getMainClass();
            if (mainClass != null && !mainClass.isBlank()) {
                assert entry != null : "Using multiple modules with main method is not supported";
                entry = vertex;
            }
        }
    }
}
