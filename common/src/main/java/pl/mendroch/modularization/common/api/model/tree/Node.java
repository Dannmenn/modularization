package pl.mendroch.modularization.common.api.model.tree;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.List.copyOf;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Node<V> {
    @EqualsAndHashCode.Include
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
    public String toString() {
        return value.toString() + " { " + children + " }";
    }
}
