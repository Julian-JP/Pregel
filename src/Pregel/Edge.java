package Pregel;

public class Edge<EV> {
    private EV value;
    int sourceId, dstId;

    public Edge(EV value, int sourceId, int dstId) {
        this.value = value;
        this.sourceId = sourceId;
        this.dstId = dstId;
    }

    public EV getValue() {
        return value;
    }
}
