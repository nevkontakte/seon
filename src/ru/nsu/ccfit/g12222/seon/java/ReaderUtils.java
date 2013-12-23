package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.EdnReader;
import clojure.lang.Util;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

public class ReaderUtils {
    static public int read1(Reader r) {
        return EdnReader.read1(r);
    }

    static void unread(PushbackReader r, int ch) {
        if (ch != -1)
            try {
                r.unread(ch);
            } catch (IOException e) {
                throw Util.sneakyThrow(e);
            }
    }
}
