package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.PersistentHashMap;
import clojure.lang.Util;
import clojure.lang.Var;

import java.io.PushbackReader;

public class ParseMap extends AbstractParser {

    @Override
    public Object invoke(Object state, PushbackReader r, char initch) {
        state = getHandler(MAP_OPEN).invoke(state);
        for(;;) {
            //
            // Read map key
            //
            int ch;

            do {
                ch = ReaderUtils.read1(r);
            } while (CharUtils.isWhitespace(ch));

            if (ch == -1) {
                throw Util.runtimeException("EOF while reading");
            }

            if(ch == '}') {
                break;
            }

            try {
                // In this case any atom will serve as map key, so we switch handlers.
                // TODO handle case when non-atomic value is provided as key.
                Var.pushThreadBindings(
                        PersistentHashMap.EMPTY.assoc(ATOM, MAP_KEY.deref())
                );
                ReaderUtils.unread(r, ch);
                state = SaxParser.read(state, r);
            } finally {
                Var.popThreadBindings();
            }

            //
            // Read map value
            //

            do {
                ch = ReaderUtils.read1(r);
            } while (CharUtils.isWhitespace(ch));

            if (ch == -1) {
                throw Util.runtimeException("EOF while reading");
            }

            if(ch == '}') {
                throw Util.runtimeException("Map literal must contain an even number of forms");
            }

            ReaderUtils.unread(r, ch);
            state = SaxParser.read(state, r);
        }
        state = getHandler(MAP_CLOSE).invoke(state);
        return state;
    }
}
