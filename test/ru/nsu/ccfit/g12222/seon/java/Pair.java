package ru.nsu.ccfit.g12222.seon.java;

/**
* Created by aleks on 12/24/13.
*/
class Pair {
    public final Object a,b;

    Pair(Object a, Object b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair pair = (Pair) o;

        return !(a != null ? !a.equals(pair.a) : pair.a != null) && !(b != null ? !b.equals(pair.b) : pair.b != null);

    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
