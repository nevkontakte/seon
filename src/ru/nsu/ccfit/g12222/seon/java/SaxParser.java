package ru.nsu.ccfit.g12222.seon.java;


import clojure.lang.*;

import java.io.PushbackReader;
import java.io.StringReader;

import static ru.nsu.ccfit.g12222.seon.java.LispReader.*;

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
    }
    /**
     * SAX parser event handlers.
     */

    protected final IFn listOpen;
    protected final IFn listClose;

    protected final IFn mapOpen;
    protected final IFn mapKey;
    protected final IFn mapClose;

    protected final IFn atom;

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

        while(CharUtils.isWhitespace(ch))
            ch = ReaderUtils.read1(r);

        if(ch == -1)
        {
            throw Util.runtimeException("EOF while reading");
        }

        if(Character.isDigit(ch))
        {
            return ParseNumber.readNumber(r, (char) ch);
        }

        IFn macroFn = getMacro(ch);
        if(macroFn != null)
        {
            Object ret = macroFn.invoke(r, (char) ch, PersistentHashMap.EMPTY);
            //no op macros return the reader
            if(ret == r)
                throw Util.runtimeException("This is unexpected..."); // TODO fix this
            return ret;
        }

        if(ch == '+' || ch == '-')
        {
            int ch2 = ReaderUtils.read1(r);
            if(Character.isDigit(ch2))
            {
                ReaderUtils.unread(r, ch2);
                Object n = ParseNumber.readNumber(r, (char) ch);
                if(RT.suppressRead())
                    return null;
                return n;
            }
            ReaderUtils.unread(r, ch2);
        }

        String token = readToken(r, (char) ch);
        if(RT.suppressRead())
            return null;
        return interpretToken(token);
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
