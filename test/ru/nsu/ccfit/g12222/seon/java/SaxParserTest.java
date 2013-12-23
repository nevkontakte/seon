package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.AFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import clojure.lang.Var;
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
                return arg2;
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
        assertEquals(1l, SaxParser.read("1"));
        assertEquals(1l, SaxParser.read("+1"));
        assertEquals(-1l, SaxParser.read("-1"));
        assertEquals(1.0, SaxParser.read("1.0"));
        assertEquals(1.0, SaxParser.read("+1.0"));
        assertEquals(-1.0, SaxParser.read("-1.0"));
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
}
