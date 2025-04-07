package expression;

import java.util.Objects;

public class Const implements ExpMod{
    private final int value;

    public Const(int value) {
        this.value = value;
    }

    @Override
    public int evaluate(int arg) {
        return value;
    }


    public int evaluate(int x, int y, int z) {
        return value;
    }

    @Override
    public String toString() {
        return value + "";
    }

    @Override
    public String toMiniString() {
        return value + "";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Const aConst = (Const) o;
        return value == aConst.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
