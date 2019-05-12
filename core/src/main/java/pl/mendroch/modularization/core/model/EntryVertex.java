package pl.mendroch.modularization.core.model;

import pl.mendroch.modularization.common.api.model.graph.Vertex;
import pl.mendroch.modularization.common.api.model.modules.Dependency;

public class EntryVertex extends Vertex<Dependency> {
    public EntryVertex() {
        super(null);
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return 1;
    }
}