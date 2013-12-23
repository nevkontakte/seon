package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.*;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Limited reimplementation of clojure.langLispReader optimized for SAX parsing.
 */
public class LispReader {
    static Pattern intPat =
            Pattern.compile(
                    "([-+]?)(?:(0)|([1-9][0-9]*)|0[xX]([0-9A-Fa-f]+)|0([0-7]+)|([1-9][0-9]?)[rR]([0-9A-Za-z]+)|0[0-9]+)(N)?");
    static Pattern ratioPat = Pattern.compile("([-+]?[0-9]+)/([0-9]+)");
    static Pattern floatPat = Pattern.compile("([-+]?[0-9]+(\\.[0-9]*)?([eE][-+]?[0-9]+)?)(M)?");
    static Pattern symbolPat = Pattern.compile("[:]?([\\D&&[^/]].*/)?(/|[\\D&&[^/]][^/]*)");

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

    static public int read1(Reader r) {
        return EdnReader.read1(r);
    }

    static boolean isWhitespace(int ch) {
        return Character.isWhitespace(ch) || ch == ',';
    }

    static Object readNumber(PushbackReader r, char initch) {
        StringBuilder sb = new StringBuilder();
        sb.append(initch);

        for (; ; ) {
            int ch = read1(r);
            if (ch == -1 || isWhitespace(ch) /*|| isMacro(ch)*/) {
                unread(r, ch);
                break;
            }
            sb.append((char) ch);
        }

        String s = sb.toString();
        Object n = matchNumber(s);
        if (n == null)
            throw new NumberFormatException("Invalid number: " + s);
        return n;
    }

    private static Object matchNumber(String s) {
        Matcher m = intPat.matcher(s);
        if (m.matches()) {
            if (m.group(2) != null) {
                if (m.group(8) != null)
                    return BigInt.ZERO;
                return Numbers.num(0);
            }
            boolean negate = (m.group(1).equals("-"));
            String n;
            int radix = 10;
            if ((n = m.group(3)) != null)
                radix = 10;
            else if ((n = m.group(4)) != null)
                radix = 16;
            else if ((n = m.group(5)) != null)
                radix = 8;
            else if ((n = m.group(7)) != null)
                radix = Integer.parseInt(m.group(6));
            if (n == null)
                return null;
            BigInteger bn = new BigInteger(n, radix);
            if (negate)
                bn = bn.negate();
            if (m.group(8) != null)
                return BigInt.fromBigInteger(bn);
            return bn.bitLength() < 64 ?
                    Numbers.num(bn.longValue())
                    : BigInt.fromBigInteger(bn);
        }
        m = floatPat.matcher(s);
        if (m.matches()) {
            if (m.group(4) != null)
                return new BigDecimal(m.group(1));
            return Double.parseDouble(s);
        }
        m = ratioPat.matcher(s);
        if (m.matches()) {
            String numerator = m.group(1);
            if (numerator.startsWith("+")) numerator = numerator.substring(1);

            return Numbers.divide(Numbers.reduceBigInt(BigInt.fromBigInteger(new BigInteger(numerator))),
                    Numbers.reduceBigInt(BigInt.fromBigInteger(new BigInteger(m.group(2)))));
        }
        return null;
    }

    static void unread(PushbackReader r, int ch) {
        if (ch != -1)
            try {
                r.unread(ch);
            } catch (IOException e) {
                throw Util.sneakyThrow(e);
            }
    }

    public static String readToken(PushbackReader r, char initch) {
        StringBuilder sb = new StringBuilder();
        sb.append(initch);

        for (; ; ) {
            int ch = read1(r);
            if (ch == -1 || isWhitespace(ch) || isTerminatingMacro(ch)) {
                unread(r, ch);
                return sb.toString();
            }
            sb.append((char) ch);
        }
    }

    static private boolean isTerminatingMacro(int ch) {
        return (ch != '#' && ch != '\'' && ch != '%' && isMacro(ch));
    }

    static private boolean isMacro(int ch) {
        return (ch < macros.length && macros[ch] != null);
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
                throw new IllegalArgumentException("Local keywords are not supported.");
            }
            boolean isKeyword = s.charAt(0) == ':';
            Symbol sym = Symbol.intern(s.substring(isKeyword ? 1 : 0));
            if (isKeyword)
                return Keyword.intern(sym);
            else {
                throw new IllegalArgumentException("Only keyword symbols are allowed here.");
            }
        }
        return null;
    }

    static IFn getMacro(int ch){
        if(ch < macros.length)
            return macros[ch];
        return null;
    }

}
