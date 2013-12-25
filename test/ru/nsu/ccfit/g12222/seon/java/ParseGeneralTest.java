package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("unchecked")
public class ParseGeneralTest {
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

        handlers = handlers.assoc(AbstractParser.LIST_OPEN, new AFn() {
            @Override
            public Object invoke(Object state) {
                List<Object> log = (List<Object>) state;
                log.add('(');
                return log;
            }
        });

        handlers = handlers.assoc(AbstractParser.LIST_CLOSE, new AFn() {
            @Override
            public Object invoke(Object state) {
                List<Object> log = (List<Object>) state;
                log.add(')');
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
                new Object[]{
                        '{',
                        "KEY", Keyword.intern("list"),
                        '(',
                        "ATOM", 1l,
                        "ATOM", 2l,
                        "ATOM", 3l,
                        ')',
                        "KEY", Keyword.intern("nil"), "ATOM", null,
                        "KEY", Keyword.intern("str"), "ATOM", "abc",
                        "KEY", Keyword.intern("int"), "ATOM", 2l,
                        "KEY", Keyword.intern("float"), "ATOM", 3.0,
                        "KEY", Keyword.intern("bool"), "ATOM", true,
                        '}',
                },
                ((List) SaxParser.read(new LinkedList(),
                        "{ :list (1 2 3), :nil nil, :str \"abc\", :int 2, :float 3.0, :bool true}"
                )).toArray()
        );

        assertArrayEquals(
                new Object[]{
                        '(',
                        '{',
                        "KEY", Keyword.intern("key1"), "ATOM", Keyword.intern("one"),
                        "KEY", Keyword.intern("key2"), "ATOM", Keyword.intern("two"),
                        '}',
                        '{',
                        "KEY", Keyword.intern("key2"), "ATOM", Keyword.intern("three"),
                        "KEY", Keyword.intern("key3"), "ATOM", Keyword.intern("four"),
                        '}',
                        ')',
                },
                ((List) SaxParser.read(new LinkedList(),
                        "({:key1 :one, :key2 :two} {:key2 :three, :key3 :four})"
                )).toArray()
        );
    }

    @Test
    public void testMassiveParsing() throws Exception {
        InputStream in = getClass().getResourceAsStream("/ru/nsu/ccfit/g12222/seon/java/parse_test.clj.example");
        PushbackReader r = new LineNumberingPushbackReader(new InputStreamReader(in));
        LinkedList state = new LinkedList();
        try {
            //noinspection InfiniteLoopStatement
            while (true) {
                SaxParser.read(state, r);
            }
        } catch (RuntimeException e) {
            assertEquals("EOF while reading", e.getMessage());
            assertEquals(1188, state.size());
            assertTrue(state.contains(Symbol.intern("valid?-type")));
        }
    }
}
