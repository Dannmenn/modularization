package pl.mendroch.modularization.common.api.model.tree;

import java.util.List;

public class Root<V> extends Node<V> {
    public Root() {
        this(null);
    }

    public Root(V value) {
        super(value);
    }

    public Root(V value, List<Node<V>> children) {
        super(value, children);
    }
}
