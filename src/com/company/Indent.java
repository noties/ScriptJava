package com.company;

import java.util.Arrays;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public class Indent {

    private final int length;

    private int count;
    private String cache;

    public Indent(int length) {
        this.length = length;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        this.cache = null;
    }

    @Override
    public String toString() {
        if (cache == null) {
            cache = initCache();
        }
        return cache;
    }

    private String initCache() {

        final char[] chars = new char[length * count];
        Arrays.fill(chars, ' ');

        return new String(chars);
    }
}
