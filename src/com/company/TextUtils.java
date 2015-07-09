package com.company;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public class TextUtils {

    private TextUtils() {}

    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }
}
