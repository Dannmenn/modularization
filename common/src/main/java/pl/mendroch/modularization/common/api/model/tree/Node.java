package pl.mendroch.modularization.common.api.model.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.List.copyOf;

public class Node<V> {
    private final V value;
    private final List<Node<V>> children = new ArrayList<>();

    public Node(V value) {
        this.value = value;
    }

    public Node(V value, List<Node<V>> children) {
        this(value);
        this.children.addAll(children);
    }

    @SafeVarargs
    public static <T> Node<T> node(T value, Node<T>... children) {
        return new Node<>(value, new ArrayList<>(Arrays.asList(children)));
    }

    public V getValue() {
        return value;
    }

    public void addChild(Node<V> node) {
        children.add(node);
    }

    public void removeChild(Node<V> node) {
        children.remove(node);
    }

    public List<Node<V>> getChildren() {
        return copyOf(children);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(value, node.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString() + " { " + children + " }";
    }
}
