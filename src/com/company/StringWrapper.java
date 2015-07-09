package com.company;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public class StringWrapper {

    private String string;

    public StringWrapper(String in) {
        this.string = in;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }
}
