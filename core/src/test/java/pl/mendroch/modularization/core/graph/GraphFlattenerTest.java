package pl.mendroch.modularization.core.graph;

import org.junit.Test;
import pl.mendroch.modularization.common.api.model.tree.Node;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static pl.mendroch.modularization.common.api.model.tree.Node.node;

public class GraphFlattenerTest {
    @Test(expected = NullPointerException.class)
    public void flattenShouldThrowException() {
        GraphFlattener<String> flattener = new GraphFlattener<>(null);

        flattener.flatten();
    }

    @Test
    public void baseGraph() {
        Node<String> d = node("d");
        Node<String> h = node("h", d);
        Node<String> g = node("g", h);
        Node<String> z = node("z", h);
        Node<String> f = node("f", g);
        Node<String> u = node("u", g, z);
        Node<String> b = node("b", u, z, f, node("c"));
        Node<String> root = node("y", d, g, b, node("x"));
        GraphFlattener<String> flattener = new GraphFlattener<>(root);

        assertEquals(Arrays.asList("y", "x", "b", "c", "f", "u", "g", "z", "h", "d"), flattener.flatten());
    }

    @Test
    public void complexGraph() {
        Node<String> l = node("l");
        Node<String> k = node("k", l);
        Node<String> c = node("c");
        Node<String> d = node("d", c, k);
        Node<String> b = node("b", d, l);
        Node<String> h = node("h", b);
        Node<String> j = node("j", h);
        Node<String> i = node("i", k, j);
        Node<String> f = node("f", h, i);
        Node<String> g = node("g", h);
        Node<String> e = node("e", f, g);
        Node<String> a = node("a", b, c, d, e);
        Node<String> root = node("x", c, a, b, e);
        GraphFlattener<String> flattener = new GraphFlattener<>(root);

        List<String> result = flattener.flatten();
        System.out.println(result);
        assertEquals(Arrays.asList("x", "a", "e", "g", "f", "i", "j", "h", "b", "d", "c", "k", "l"), result);
    }
}