package pl.mendroch.modularization.core.graph;

import pl.mendroch.modularization.common.api.model.tree.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphFlattener<T> {
    private final Map<Node<T>, NodeStatistics> statistics = new HashMap<>();
    private final Node<T> root;

    public GraphFlattener(Node<T> root) {
        this.root = root;
        calculateStatistics(root);
    }

    private void calculateStatistics(Node<T> root) {
        if (statistics.containsKey(root)) {
            statistics.get(root).in++;
            return;
        }
        statistics.put(root, new NodeStatistics(root));
        for (Node<T> child : root.getChildren()) {
            calculateStatistics(child);
        }
    }

    public List<T> flatten() {
        List<T> result = new LinkedList<>();
        Node<T> tmp = this.root;
        while (!tmp.getChildren().isEmpty()) {
            if (tmp.getChildren().size() == 1) {
                result.add(tmp.getValue());
                tmp = tmp.getChildren().get(0);
            } else {
                NodeStatistics candidate = null;
                for (Node<T> child : tmp.getChildren()) {
                    NodeStatistics nodeStatistics = statistics.get(child);
                    if (candidate == null || nodeStatistics.compareTo(candidate) > 0) {
                        candidate = nodeStatistics;
                    }
                }
                assert candidate != null;
                Node<T> node = candidate.node;
                statistics.remove(node);
                List<Node<T>> nextChildren = node.getChildren();
                for (Node<T> child : tmp.getChildren()) {
                    if (child.equals(node)) continue;
                    NodeStatistics childStatistics = statistics.get(child);
                    childStatistics.addCurrentState();
                    if (nextChildren.contains(child)) {
                        childStatistics.in--;
                    } else {
                        node.addChild(child);
                    }
                }
                result.add(tmp.getValue());
                tmp = node;
            }
        }
        result.add(tmp.getValue());
        return result;
    }

    private class NodeStatistics implements Comparable<NodeStatistics> {
        private final Node<T> node;
        private int in = 0;
        private int out;
        private int sum = 0;

        private NodeStatistics(Node<T> node) {
            this.node = node;
            out = node.getChildren().size();
        }

        private void addCurrentState() {
            sum += in + out;
        }

        @Override
        public int compareTo(NodeStatistics o) {
            int result = Integer.compare(o.in, in);
            if (result != 0) return result;
            result = Integer.compare(o.out, out);
            if (result != 0) return result;
            result = Integer.compare(sum, o.sum);
            if (result != 0) return result;
            return node.toString().compareTo(o.node.toString());
        }
    }
}
