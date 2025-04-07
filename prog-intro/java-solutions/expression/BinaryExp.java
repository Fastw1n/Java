package expression;

import java.util.Objects;

public abstract class BinaryExp implements ExpMod {
    protected final ExpMod first;
    protected final ExpMod second;
    private final char act;

    public BinaryExp(ExpMod first, ExpMod second, char action) {
        this.first = first;
        this.second = second;
        act = action;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) {
            return false;
        }
        BinaryExp object = (BinaryExp) o;
        return act == object.act && this.first.equals(object.first) && this.second.equals(object.second);
    }



    public int hashCode() {
        return Objects.hash(first, second, act);
    }
}
