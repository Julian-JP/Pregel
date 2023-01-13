package Pregel;

public class Node<NV> {
    private NV value;
    int id;

    public Node(NV value, int id) {
        this.value = value;
        this.id = id;
    }

    public NV getValue() {
        return value;
    }

    public void setValue(NV value) {
        this.value = value;
    }

    public int getId() {return id;}
}
