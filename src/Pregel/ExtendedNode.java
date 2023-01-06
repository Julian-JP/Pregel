package Pregel;

import java.util.ArrayList;
import java.util.List;

public class ExtendedNode<NV,EV> {
    Node<NV> node;
    private List<EdgeConnector<NV, EV>> neighbors;

    public ExtendedNode(Node<NV> node, List<EdgeConnector<NV, EV>> neighbors) {
        this.node = node;
        this.neighbors = neighbors;
    }

    public ExtendedNode(Node<NV> node) {
        this.node = node;
        this.neighbors = new ArrayList<>();
    }

    public void addEdge(Edge<EV> edge, ExtendedNode<NV, EV> to) {
        neighbors.add(new EdgeConnector<NV, EV>(to, edge));
    }

    public List<EdgeConnector<NV, EV>> getNeighbors() {
        return neighbors;
    }

    public NV getValue() {
        return node.getValue();
    }

    public void setValue(NV value) {
        node.setValue(value);
    }
}
