package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public class ConsoleReader {

    public interface Callback {
        void onNewLine(String line);
    }

    private volatile boolean isRunning = true;

    public void read(final Callback callback) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (isRunning) {
            try {
                final String line = reader.readLine();
                if (line != null) {
                    callback.onNewLine(line);
                }
            } catch (IOException e) {

            }
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
