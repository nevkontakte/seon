package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaxParserAtomicTest {
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
        assertEquals(new Pair(Keyword.class, Keyword.intern("testkwd2")), SaxParser.read(":testkwd2"));
        assertEquals("testkwd", ((Keyword) ((Pair) SaxParser.read(":testkwd")).b).getName());
    }

    @Test
    public void testSymbol() throws Exception {
        assertEquals(new Pair(Symbol.class, Symbol.intern("+")), SaxParser.read("+"));
        assertEquals(new Pair(Symbol.class, Symbol.intern("+one?")), SaxParser.read("+one?"));
        assertEquals(new Pair(Symbol.class, Symbol.intern("abc")), SaxParser.read("abc"));
        assertEquals(new Pair(Symbol.class, Symbol.intern("testkwd")), SaxParser.read("testkwd"));
        assertEquals(new Pair(Symbol.class, Symbol.intern("testkwd2")), SaxParser.read("testkwd2"));
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

}
