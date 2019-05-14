package pl.mendroch.modularization.core.graph;

import org.junit.Test;
import pl.mendroch.modularization.common.api.model.tree.Node;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static pl.mendroch.modularization.common.api.model.tree.Node.node;

public class GraphFlattenerTest {
    @Test(expected = NullPointerException.class)
    public void flattenShouldThrowException() {
        GraphFlattener<String> flattener = new GraphFlattener<>(null);

        flattener.flatten();
    }

    @Test
    public void baseCase() {
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
}