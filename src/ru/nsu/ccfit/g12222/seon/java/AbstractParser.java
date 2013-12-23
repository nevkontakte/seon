package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;

import java.io.PushbackReader;

public abstract class AbstractParser extends AFn {
    protected final static Noop noop = new Noop();

    public static final Namespace SAX_NS = Namespace.findOrCreate(Symbol.intern("clojure.core"));

    /**
     * "List opened" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    public static final Var LIST_OPEN = Var.intern(SAX_NS, Symbol.intern("*seon-list-open*"), noop).setDynamic();
    /**
     * "List closed" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    public static final Var LIST_CLOSE = Var.intern(SAX_NS, Symbol.intern("*seon-list-close*"), noop).setDynamic();
    /**
     * "Map opened" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    public static final Var MAP_OPEN = Var.intern(SAX_NS, Symbol.intern("*seon-map-open*"), noop).setDynamic();
    /**
     * "Map closed" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    public static final Var MAP_CLOSE = Var.intern(SAX_NS, Symbol.intern("*seon-map-close*"), noop).setDynamic();
    /**
     * "Map key" event handler.
     *
     * Clojure function, accepting two parameters: state and met key name. Must return new state.
     * (fn [state keyName] state)
     */
    public static final Var MAP_KEY = Var.intern(SAX_NS, Symbol.intern("*seon-map-key*"), noop).setDynamic();
    /**
     * "Atomic value" event handler, e.g. string, number, integer, boolean, nil.
     *
     * Clojure function, accepting two parameters: state and value. Must return new state.
     * (fn [state value] state)
     */
    public static final Var ATOM = Var.intern(SAX_NS, Symbol.intern("*seon-atom*"), noop).setDynamic();

    protected static IFn getHandler(Var name) {
        Object handler = name.deref();
        return (handler == null) ? noop : (IFn) handler;
    }

    /**
     * Stub handler in case user didn't provide his own.
     */
    protected static class Noop extends AFn {
        @Override
        public Object invoke(Object arg1, Object arg2) {
            return arg1;
        }

        @Override
        public Object invoke(Object arg1) {
            return arg1;
        }
    }

    public abstract Object invoke(Object state, PushbackReader r, char initch);

    @Override
    public Object invoke(Object state, Object r, Object initch) {
        return this.invoke(state, (PushbackReader) r, (char) initch);
    }
}
