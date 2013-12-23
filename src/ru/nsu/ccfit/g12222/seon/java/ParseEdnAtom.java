package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.EdnReader;
import clojure.lang.IFn;
import clojure.lang.PersistentHashMap;

import java.io.PushbackReader;

public class ParseEdnAtom extends AbstractParser {
    private final IFn reader;

    public ParseEdnAtom(IFn reader) {
        this.reader = reader;
    }

    @Override
    public Object invoke(Object state, PushbackReader r, char initch) {
        Object string = reader.invoke(r, initch, PersistentHashMap.EMPTY);
        return getHandler(ATOM).invoke(state, string);
    }
}
