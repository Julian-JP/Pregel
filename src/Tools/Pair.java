package Tools;

public class Pair<A, B> {
    private A v1;
    private B v2;

    public Pair(A v1, B v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public A first() {
        return v1;
    }

    public B second() {
        return v2;
    }

    public void setFirst(A a) {
        v1 = a;
    }

    public void setSecond(B b) {
        v2 = b;
    }
}
