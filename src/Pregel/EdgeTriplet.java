package Pregel;

public class EdgeTriplet<NV, EV> {
    final ExtendedNode<NV, EV> from, to;
    final Edge<EV> edge;

    public EdgeTriplet(ExtendedNode<NV, EV> from, ExtendedNode<NV, EV> to, Edge<EV> edge) {
        this.from = from;
        this.to = to;
        this.edge = edge;
    }

    public NV srcAttr() {
        return from.getValue();
    }

    public NV dstAttr() {
        return to.getValue();
    }

    public int srcId() {
        return from.node.id;
    }

    public int dstId() {
        return to.node.id;
    }

    public EV edgeAttr() {
        return edge.getValue();
    }

    public int srcNodeDegree() {
        return from.getNeighbors().size();
    }

    public int dstNodeDegree() {
        return to.getNeighbors().size();
    }
}
