package ru.nsu.ccfit.g12222.seon.java;

public class CharUtils {
    static boolean isWhitespace(int ch) {
        return Character.isWhitespace(ch) || ch == ',';
    }
}
