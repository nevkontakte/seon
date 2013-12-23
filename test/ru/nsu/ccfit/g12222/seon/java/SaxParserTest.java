package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.Keyword;
import org.junit.Test;

import static org.junit.Assert.*;

public class SaxParserTest {
    @Test
    public void testReadNumber() throws Exception {
        assertEquals(1l, SaxParser.read("1"));
        assertEquals(1l, SaxParser.read("+1"));
        assertEquals(-1l, SaxParser.read("-1"));
        assertEquals(1.0, SaxParser.read("1.0"));
        assertEquals(1.0, SaxParser.read("+1.0"));
        assertEquals(-1.0, SaxParser.read("-1.0"));
    }

    @Test
    public void testReadNil() throws Exception {
        assertNull(SaxParser.read("nil"));
    }

    @Test
    public void testReadBool() throws Exception {
        assertTrue((Boolean) SaxParser.read("true"));
        assertFalse((Boolean) SaxParser.read("false"));
    }

    @Test
    public void testReadKeyword() throws Exception {
        assertEquals(Keyword.intern("testkwd"), SaxParser.read(":testkwd"));
        assertEquals("testkwd", ((Keyword) SaxParser.read(":testkwd")).getName());
    }

    @Test
    public void testReadString() throws Exception {
        assertEquals("", SaxParser.read("\"\""));
        assertEquals("abc", SaxParser.read("\"abc\""));
        assertEquals("a\nbc", SaxParser.read("\"a\\nbc\""));
        assertEquals("a\nbc", SaxParser.read("\"a\nbc\""));
        assertEquals("a\u00FFbc", SaxParser.read("\"a\u00FFbc\""));
        assertEquals("a\u00FFbc", SaxParser.read("\"a\\u00FFbc\""));

    }
}
