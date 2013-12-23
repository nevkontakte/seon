package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaxParserTest {
    @Before
    public void setUp() throws Exception {
        IPersistentMap handlers = PersistentHashMap.create();
        handlers = handlers.assoc(AbstractParser.ATOM, new AFn() {
            @Override
            public Object invoke(Object state, Object value) {
                return new Pair((value == null) ? null : value.getClass(), value);
            }
        });
        Var.pushThreadBindings(handlers);
    }

    @After
    public void tearDown() throws Exception {
        Var.popThreadBindings();
    }

    @Test
    public void testReadNumber() throws Exception {
        assertEquals(new Pair(Long.class, 1l), SaxParser.read("1"));
        assertEquals(new Pair(Long.class, 1l), SaxParser.read("+1"));
        assertEquals(new Pair(Long.class, -1l), SaxParser.read("-1"));
        assertEquals(new Pair(Double.class, 1.0), SaxParser.read("1.0"));
        assertEquals(new Pair(Double.class, 1.0), SaxParser.read("+1.0"));
        assertEquals(new Pair(Double.class, -1.0), SaxParser.read("-1.0"));
    }

    @Test
    public void testReadNil() throws Exception {
        assertEquals(new Pair(null, null), SaxParser.read("nil"));
    }

    @Test
    public void testReadBool() throws Exception {
        assertEquals(new Pair(Boolean.class, true), SaxParser.read("true"));
        assertEquals(new Pair(Boolean.class, false), SaxParser.read("false"));
    }

    @Test
    public void testReadKeyword() throws Exception {
        assertEquals(new Pair(Keyword.class, Keyword.intern("testkwd")), SaxParser.read(":testkwd"));
        assertEquals("testkwd", ((Keyword) ((Pair) SaxParser.read(":testkwd")).b).getName());
    }

    @Test
    public void testSymbol() throws Exception {
        assertEquals(new Pair(Symbol.class, Symbol.intern("+")), SaxParser.read("+"));
        assertEquals(new Pair(Symbol.class, Symbol.intern("abc")), SaxParser.read("abc"));
        assertEquals(new Pair(Symbol.class, Symbol.intern("testkwd")), SaxParser.read("testkwd"));
    }


    @Test
    public void testReadString() throws Exception {
        assertEquals(new Pair(String.class, ""), SaxParser.read("\"\""));
        assertEquals(new Pair(String.class, "abc"), SaxParser.read("\"abc\""));
        assertEquals(new Pair(String.class, "a\nbc"), SaxParser.read("\"a\\nbc\""));
        assertEquals(new Pair(String.class, "a\nbc"), SaxParser.read("\"a\nbc\""));
        assertEquals(new Pair(String.class, "a\u00FFbc"), SaxParser.read("\"a\u00FFbc\""));
        assertEquals(new Pair(String.class, "a\u00FFbc"), SaxParser.read("\"a\\u00FFbc\""));

    }

    private static class Pair{
        public final Object a,b;

        private Pair(Object a, Object b) {
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
}
