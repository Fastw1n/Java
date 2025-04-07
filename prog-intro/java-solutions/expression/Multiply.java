package expression;

public class Multiply extends BinaryExp {

    private final String str;

    public Multiply(ExpMod first, ExpMod second) {
        super(first, second, '*');
        str = "(" + first.toString() + " * " + second.toString() + ")";
    }
    @Override
    public int evaluate(int arg) {
        return first.evaluate(arg) * second.evaluate(arg);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return first.evaluate( x,  y,  z) * second.evaluate( x,  y,  z);
    }

    @Override
    public String toString() {
        return str;
    }

    @Override
    public String toMiniString() {
        return str;
    }

//    @Override
//    public boolean equals(Object obj) {
//        return super.equals(obj);
//    }

//    @Override
//    public int hashCode() {
//        return super.hashCode('*');
//    }
}
