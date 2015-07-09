package com.company;

public class Main {

    public static void main(String[] args) {
        final ConsoleReader reader = new ConsoleReader();
        final ScriptWriter writer = new ScriptWriter();
        reader.read(writer);
    }
}
