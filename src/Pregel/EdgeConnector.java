package Pregel;

public class EdgeConnector<NV, EV> {
    ExtendedNode<NV, EV> to;
    Edge<EV> edge;

    public EdgeConnector(ExtendedNode<NV, EV> to, Edge<EV> edge) {
        this.to = to;
        this.edge = edge;
    }
}
