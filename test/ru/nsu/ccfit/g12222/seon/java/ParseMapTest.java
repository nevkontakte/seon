package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

@SuppressWarnings("unchecked")
public class ParseMapTest {
    @Before
    public void setUp() throws Exception {
        IPersistentMap handlers = PersistentHashMap.create();

        handlers = handlers.assoc(AbstractParser.ATOM, new AFn() {
            @Override
            public Object invoke(Object state, Object value) {
                List<Object> log = (List<Object>) state;
                log.add("ATOM");
                log.add(value);
                return log;
            }
        });

        handlers = handlers.assoc(AbstractParser.MAP_OPEN, new AFn() {
            @Override
            public Object invoke(Object state) {
                List<Object> log = (List<Object>) state;
                log.add('{');
                return log;
            }
        });

        handlers = handlers.assoc(AbstractParser.MAP_KEY, new AFn() {
            @Override
            public Object invoke(Object state, Object value) {
                List<Object> log = (List<Object>) state;
                log.add("KEY");
                log.add(value);
                return log;
            }
        });

        handlers = handlers.assoc(AbstractParser.MAP_CLOSE, new AFn() {
            @Override
            public Object invoke(Object state) {
                List<Object> log = (List<Object>) state;
                log.add('}');
                return log;
            }
        });

        Var.pushThreadBindings(handlers);
    }

    @After
    public void tearDown() throws Exception {
        Var.popThreadBindings();
    }

    @Test
    public void testInvoke() throws Exception {
        assertArrayEquals(
                new Object[]{'{', '}'},
                ((List) SaxParser.read(new LinkedList(), "{}")).toArray()
        );

        assertArrayEquals(
                new Object[]{'{', '}'},
                ((List) SaxParser.read(new LinkedList(), "{ }")).toArray()
        );

        assertArrayEquals(
                new Object[]{'{', '}'},
                ((List) SaxParser.read(new LinkedList(), "{ , , }")).toArray()
        );

        assertArrayEquals(
                new Object[]{'{', "KEY", Keyword.intern("k1"), "ATOM", 123l, '}'},
                ((List) SaxParser.read(new LinkedList(), "{ :k1 123 }")).toArray()
        );

        assertArrayEquals(
                new Object[]{'{', "KEY", Keyword.intern("k1"), "ATOM", 123l, '}'},
                ((List) SaxParser.read(new LinkedList(), "{ :k1, 123, }")).toArray()
        );

        assertArrayEquals(
                new Object[]{'{', "KEY", Keyword.intern("k1"), "ATOM", 123l, '}'},
                ((List) SaxParser.read(new LinkedList(), "{ :k1 123}")).toArray()
        );

        assertArrayEquals(
                new Object[]{'{', "KEY", Keyword.intern("k1"), '{', "KEY", Keyword.intern("k2"), "ATOM", 456l, '}', '}'},
                ((List) SaxParser.read(new LinkedList(), "{ :k1 { :k2 456 }}")).toArray()
        );

    }
}
