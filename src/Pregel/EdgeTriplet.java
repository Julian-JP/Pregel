package Pregel;

public class EdgeTriplet<NV, EV> {
    final Node<NV> from, to;
    final Edge<EV> edge;

    public EdgeTriplet(Node<NV> from, Node<NV> to, Edge<EV> edge) {
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
        return from.id;
    }

    public int dstId() {
        return to.id;
    }

    public EV edgeAttr() {
        return edge.getValue();
    }
}
