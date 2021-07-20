package net.badbird5907.jdacommand.util.object;

public class Triplet<X, Y,Z> {
    private final X x;
    private final Y y;
    private final Z z;
    public Triplet(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public X getValue0() {
        return x;
    }

    public Y getValue1() {
        return y;
    }
    public Z getValue2(){
        return z;
    }
}