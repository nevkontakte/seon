package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.AFn;
import clojure.lang.IPersistentMap;
import clojure.lang.PersistentHashMap;
import clojure.lang.Var;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class ParseListTest {
    @Before
    public void setUp() throws Exception {
        IPersistentMap handlers = PersistentHashMap.create();

        handlers = handlers.assoc(AbstractParser.LIST_OPEN, new AFn() {
            @Override
            public Object invoke(Object state) {
                List<Object> log = (List<Object>) state;
                log.add('(');
                return log;
            }
        });

        handlers = handlers.assoc(AbstractParser.ATOM, new AFn() {
            @Override
            public Object invoke(Object state, Object value) {
                List<Object> log = (List<Object>) state;
                log.add(value);
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
                new Object[]{'(', ')'},
               ((List) SaxParser.read(new LinkedList(), "()")).toArray()
        );

        assertArrayEquals(
                new Object[]{'(', 1l, null, "abc", ')'},
               ((List) SaxParser.read(new LinkedList(), "(1 nil \"abc\")")).toArray()
        );

        assertArrayEquals(
                new Object[]{'(', 1l, '(', null, ')', 2.0, ')'},
               ((List) SaxParser.read(new LinkedList(), "(1 (nil) 2.0)")).toArray()
        );
    }
}
