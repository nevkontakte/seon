package ru.nsu.ccfit.g12222.seon.java;

import clojure.lang.Util;

import java.io.PushbackReader;

public class ParseList extends AbstractParser {
    @Override
    public Object invoke(Object state, PushbackReader r, char initch) {
        state = getHandler(LIST_OPEN).invoke(state);
        for(;;) {
            int ch = ReaderUtils.read1(r);

            if (ch == -1) {
                throw Util.runtimeException("EOF while reading");
            }

            if(ch == ')') {
                break;
            }

            ReaderUtils.unread(r, ch);

            state = SaxParser.read(state, r);
        }
        state = getHandler(LIST_CLOSE).invoke(state);
        return state;
    }
}
