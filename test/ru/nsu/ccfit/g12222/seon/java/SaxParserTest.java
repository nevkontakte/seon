package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SaxParserTest {
    @Before
    public void setUp() throws Exception {
        IPersistentMap handlers = PersistentHashMap.create();
        handlers = handlers.assoc(AbstractParser.ATOM, new AFn() {
            @Override
            public Object invoke(Object arg1, Object arg2) {
                return new Pair(arg2.getClass(), arg2);
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
//
//    @Test
//    public void testReadNil() throws Exception {
//        assertNull(SaxParser.read("nil"));
//    }
//
//    @Test
//    public void testReadBool() throws Exception {
//        assertTrue((Boolean) SaxParser.read("true"));
//        assertFalse((Boolean) SaxParser.read("false"));
//    }
//
//    @Test
//    public void testReadKeyword() throws Exception {
//        assertEquals(Keyword.intern("testkwd"), SaxParser.read(":testkwd"));
//        assertEquals("testkwd", ((Keyword) SaxParser.read(":testkwd")).getName());
//    }
//
//    @Test
//    public void testReadString() throws Exception {
//        assertEquals("", SaxParser.read("\"\""));
//        assertEquals("abc", SaxParser.read("\"abc\""));
//        assertEquals("a\nbc", SaxParser.read("\"a\\nbc\""));
//        assertEquals("a\nbc", SaxParser.read("\"a\nbc\""));
//        assertEquals("a\u00FFbc", SaxParser.read("\"a\u00FFbc\""));
//        assertEquals("a\u00FFbc", SaxParser.read("\"a\\u00FFbc\""));
//
//    }

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
