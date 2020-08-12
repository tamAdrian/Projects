package utils;

import java.util.Objects;

public class Pair<Type1, Type2> {
    private Type1 a;
    private Type2 b;

    public Pair(Type1 a, Type2 b) {
        this.a = a;
        this.b = b;
    }

    public Type1 getA() {
        return a;
    }

    public void setA(Type1 a) {
        this.a = a;
    }

    public Type2 getB() {
        return b;
    }

    public void setB(Type2 b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(a, pair.a) &&
                Objects.equals(b, pair.b);
    }

}
