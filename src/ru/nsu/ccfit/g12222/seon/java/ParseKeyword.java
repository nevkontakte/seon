package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;

import java.io.PushbackReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseKeyword extends AFn {
    static Pattern symbolPat = Pattern.compile("[:]?([\\D&&[^/]].*/)?(/|[\\D&&[^/]][^/]*)");

    public static String readToken(PushbackReader r, char initch) {
        StringBuilder sb = new StringBuilder();
        sb.append(initch);

        for (; ; ) {
            int ch = ReaderUtils.read1(r);
            if (ch == -1 || CharUtils.isWhitespace(ch) || isTerminatingMacro(ch)) {
                ReaderUtils.unread(r, ch);
                return sb.toString();
            }
            sb.append((char) ch);
        }
    }

    public static Object interpretToken(String s) {
        switch (s) {
            case "nil":
                return null;
            case "true":
                return RT.T;
            case "false":
                return RT.F;
        }
        Object ret;

        ret = matchSymbol(s);
        if (ret != null)
            return ret;

        throw Util.runtimeException("Invalid token: " + s);
    }

    private static Object matchSymbol(String s) {
        Matcher m = symbolPat.matcher(s);

        if (m.matches()) {
            String ns = m.group(1);
            String name = m.group(2);
            if (ns != null && ns.endsWith(":/")
                    || name.endsWith(":")
                    || s.indexOf("::", 1) != -1)
                return null;
            if (s.startsWith("::")) {
                return null;
            }
            boolean isKeyword = s.charAt(0) == ':';
            Symbol sym = Symbol.intern(s.substring(isKeyword ? 1 : 0));
            if (isKeyword)
                return Keyword.intern(sym);
            return sym;
        }
        return null;
    }

    static boolean isTerminatingMacro(int ch) {
        return (ch != '#' && ch != '\'' && (SaxParser.getMacro(ch) != null));
    }

    @Override
    public Object invoke(Object r, Object initch, Object __reserved__) {
        return invoke((PushbackReader) r, (char) initch, __reserved__);
    }

    public Object invoke(PushbackReader r, char initch, Object __reserved__) {
        return interpretToken(readToken(r, initch));
    }
}
