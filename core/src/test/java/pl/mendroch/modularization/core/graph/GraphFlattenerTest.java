package pl.mendroch.modularization.core.graph;

import org.junit.Test;
import pl.mendroch.modularization.common.api.model.graph.Vertex;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static pl.mendroch.modularization.common.api.model.graph.Vertex.vertexOf;

public class GraphFlattenerTest {
    private Map<Vertex<String>, List<Vertex<String>>> edges = new HashMap<>();

    @Test
    public void baseGraph() {
        Vertex<String> d = vertexOf("d");
        Vertex<String> h = vertexOf("h");
        addEdge(d, h);
        Vertex<String> g = vertexOf("g");
        addEdge(h, g);
        Vertex<String> z = vertexOf("z");
        addEdge(h, z);
        Vertex<String> f = vertexOf("f");
        addEdge(g, f);
        Vertex<String> u = vertexOf("u");
        addEdge(g, u);
        addEdge(z, u);
        Vertex<String> b = vertexOf("b");
        addEdge(u, b);
        addEdge(z, b);
        addEdge(f, b);
        addEdge(vertexOf("c"), b);
        Vertex<String> root = vertexOf("y");
        addEdge(d, root);
        addEdge(g, root);
        addEdge(b, root);
        addEdge(vertexOf("x"), root);
        GraphFlattener<String> flattener = new GraphFlattener<>(edges, root);

        assertEquals(Arrays.asList("d", "h", "g", "z", "u", "f", "c", "b", "x", "y"), flattener.flatten());
    }

    private void addEdge(Vertex<String> to, Vertex<String> from) {
        edges.computeIfAbsent(from, stringVertex -> new ArrayList<>()).add(to);
    }

    @Test
    public void complexGraph() {
        Vertex<String> l = vertexOf("l");
        Vertex<String> k = vertexOf("k");
        addEdge(l, k);
        Vertex<String> c = vertexOf("c");
        Vertex<String> d = vertexOf("d");
        addEdge(c, d);
        addEdge(k, d);
        Vertex<String> b = vertexOf("b");
        addEdge(d, b);
        addEdge(l, b);
        Vertex<String> h = vertexOf("h");
        addEdge(b, h);
        Vertex<String> j = vertexOf("j");
        addEdge(h, j);
        Vertex<String> i = vertexOf("i");
        addEdge(k, i);
        addEdge(j, i);
        Vertex<String> f = vertexOf("f");
        addEdge(h, f);
        addEdge(i, f);
        Vertex<String> g = vertexOf("g");
        addEdge(h, g);
        Vertex<String> e = vertexOf("e");
        addEdge(f, e);
        addEdge(g, e);
        Vertex<String> a = vertexOf("a");
        addEdge(b, a);
        addEdge(c, a);
        addEdge(d, a);
        addEdge(e, a);
        Vertex<String> root = vertexOf("x");
        addEdge(c, root);
        addEdge(a, root);
        addEdge(b, root);
        addEdge(e, root);

        GraphFlattener<String> flattener = new GraphFlattener<>(edges, root);
        flattener.flatten();

        assertEquals(Arrays.asList("c", "l", "k", "d", "b", "h", "j", "i", "f", "g", "e", "a", "x"), flattener.flatten());
    }
}