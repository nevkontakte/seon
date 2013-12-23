package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.EdnReader;
import clojure.lang.PersistentHashMap;

import java.io.PushbackReader;

public class ParseString extends AbstractParser {
    private static final EdnReader.StringReader reader = new EdnReader.StringReader();

    @Override
    public Object invoke(Object state, PushbackReader r, char initch) {
        Object string = reader.invoke(r, initch, PersistentHashMap.EMPTY);
        return getHandler(ATOM).invoke(state, string);
    }
}
