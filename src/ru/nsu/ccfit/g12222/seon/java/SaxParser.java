package ru.nsu.ccfit.g12222.seon.java;


import clojure.lang.IFn;
import clojure.lang.Util;

import java.io.PushbackReader;
import java.io.StringReader;

/**
 * Parse SEON expression in SAX manner.
 */
public class SaxParser extends AbstractParser {

    static final AbstractParser[] macros = new AbstractParser[256];

    public static final char STRING = '"';

    public static final char COMMENT = ';';

    public static final char LIST1 = '(';

    public static final char LIST2 = ')';

    public static final char MAP1 = '{';

    public static final char MAP2 = '}';

    public static final char CHAR = '\\';

    public static final char KEYWORD = ':';

    public static final char NUMBER_PLUS = '+';

    public static final char NUMBER_MINUS = '-';

    public static final char DIGIT = '0';

    static {
//        macros[STRING] = new LispReader.StringReader();
//        macros[COMMENT] = new LispReader.CommentReader();
//        macros[LIST1] = new LispReader.ListReader();
//        macros[LIST2] = new LispReader.UnmatchedDelimiterReader();
//        macros[MAP1] = new LispReader.MapReader();
//        macros[MAP2] = new LispReader.UnmatchedDelimiterReader();
//        macros[CHAR] = new LispReader.CharacterReader();
//        macros[KEYWORD] = new ParseKeyword();
        macros[NUMBER_PLUS] = macros[NUMBER_MINUS] = macros[DIGIT] = new ParseNumber();
    }

    public static Object read(String str) {
        return read(null, str);
    }

    public static Object read(Object state, String str) {
        return read(state, new PushbackReader(new StringReader(str)));
    }

    public static Object read(Object state, PushbackReader r) {
        int ch = ReaderUtils.read1(r);

        while (CharUtils.isWhitespace(ch)) {
            ch = ReaderUtils.read1(r);
        }

        if (ch == -1) {
            throw Util.runtimeException("EOF while reading");
        }

        IFn macroFn = getMacro(ch);
        if (macroFn != null) {
            return macroFn.invoke(state, r, (char) ch);
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
        if(Character.isDigit(ch)) {
            return macros[DIGIT];
        }
        if (ch < macros.length)
            return macros[ch];
        return null;
    }


    @Override
    public Object invoke(Object state, PushbackReader r, char initch) {
        return read(state, r);
    }
}
