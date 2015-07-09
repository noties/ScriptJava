package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public class ProcessRedirect {

    public interface Callback {
        void apply();
    }

    public static void redirect(Process process) {
        redirect(process.getInputStream(), null);
        redirect(process.getErrorStream(), null);
    }

    public static void redirect(InputStream inputStream, Callback callback) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while (!TextUtils.isEmpty((line = reader.readLine()))) {
                System.out.println(line);
                if (callback != null) {
                    callback.apply();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
