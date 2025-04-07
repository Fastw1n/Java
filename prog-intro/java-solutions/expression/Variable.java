package expression;

import java.util.Objects;

public class Variable implements ExpMod{
    private final String name;


    public Variable(String string) {
        this.name = string;
    }

    @Override
    public int evaluate(int arg) {
        return arg;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        if(name.equals("x")) {
            return x;
        }
         if (name.equals("y")) {
            return y;
        }
         return z;

    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toMiniString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Variable variable = (Variable) o;
        return Objects.equals(name, variable.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
