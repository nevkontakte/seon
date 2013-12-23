package ru.nsu.ccfit.g12222.seon.java;


import clojure.lang.*;

import java.io.PushbackReader;
import java.io.StringReader;

/**
 * Parse SEON expression in SAX manner.
 */
public class SaxParser {
    static IFn[] macros = new IFn[256];

    static {
        macros['"'] = new EdnReader.StringReader();
        macros[';'] = new EdnReader.CommentReader();
        macros['('] = new EdnReader.ListReader();
        macros[')'] = new EdnReader.UnmatchedDelimiterReader();
        macros['{'] = new EdnReader.MapReader();
        macros['}'] = new EdnReader.UnmatchedDelimiterReader();
        macros['\\'] = new EdnReader.CharacterReader();
        macros[':'] = new ParseKeyword();
    }

    /**
     * SAX parser event handlers.
     */

    /**
     * "List opened" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    protected final IFn listOpen;

    /**
     * "List closed" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    protected final IFn listClose;

    /**
     * "Map opened" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    protected final IFn mapOpen;

    /**
     * "Map closed" event handler.
     *
     * Clojure function, accepting single parameter state and returning new state.
     * (fn [state] state)
     */
    protected final IFn mapClose;

    /**
     * "Map key" event handler.
     *
     * Clojure function, accepting two parameters: state and met key name. Must return new state.
     * (fn [state keyName] state)
     */
    protected final IFn mapKey;

    /**
     * "Atomic value" event handler, e.g. string, number, integer, boolean, nil.
     *
     * Clojure function, accepting two parameters: state and value. Must return new state.
     * (fn [state value] state)
     */
    protected final IFn atom;

    public SaxParser() {
        this(null, null, null, null, null, null);
    }

    public SaxParser(IFn listOpen, IFn listClose, IFn mapOpen, IFn mapKey, IFn mapClose, IFn atom) {
        this.listOpen = listOpen == null ? noop : listOpen;
        this.listClose = listClose == null ? noop : listClose;
        this.mapOpen = mapOpen == null ? noop : mapOpen;
        this.mapKey = mapKey == null ? noop : mapKey;
        this.mapClose = mapClose == null ? noop : mapClose;
        this.atom = atom == null ? noop : atom;
    }

    public static Object read(String str) {
        return read(new PushbackReader(new StringReader(str)));
    }

    public static Object read(PushbackReader r) {
        int ch = ReaderUtils.read1(r);

        while (CharUtils.isWhitespace(ch)) {
            ch = ReaderUtils.read1(r);
        }

        if (ch == -1) {
            throw Util.runtimeException("EOF while reading");
        }

        if (Character.isDigit(ch)) {
            return ParseNumber.readNumber(r, (char) ch);
        }

        IFn macroFn = getMacro(ch);
        if (macroFn != null) {
            Object ret = macroFn.invoke(r, (char) ch, PersistentHashMap.EMPTY);
            return ret;
        }

        if (ch == '+' || ch == '-') {
            int ch2 = ReaderUtils.read1(r);
            if (Character.isDigit(ch2)) {
                ReaderUtils.unread(r, ch2);
                Object n = ParseNumber.readNumber(r, (char) ch);
                return n;
            }
            ReaderUtils.unread(r, ch2);
        }

        return ParseKeyword.interpretToken(ParseKeyword.readToken(r, (char) ch));
    }

    static IFn getMacro(int ch) {
        if (ch < macros.length)
            return macros[ch];
        return null;
    }


    /**
     * Stub handler in case user didn't provide his own.
     */
    protected static class Noop extends AFn {
        @Override
        public Object throwArity(int n) {
            return null;
        }
    }

    protected final static Noop noop = new Noop();

}
