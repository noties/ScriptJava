/*
 * Copyright 2016 Dimitry Ivanov (copy@dimitryivanov.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package scriptjava.buildins;

import java.io.File;
import java.util.Collection;
import java.util.Map;

/**
 * A helper class for basic Java types that can have a length parameter.
 * The main idea is unification between different ways to retrieve object's length.
 * Each method accepts null as a parameter (in case of null the method will return just `0`).
 * Also contains a helper method to get the length of the File (in case of the folder
 * will return the size of it's contents)
 *
 * Inspired by Python's build-in
 *
 */
public class Length {

    public static <T extends CharSequence> int len(T value) {
        return value != null ? value.length() : 0;
    }

    public static <T extends Collection<?>> int len(T value) {
        return value != null ? value.size() : 0;
    }

    public static <T extends Map<?, ?>> int len(T value) {
        return value != null ? value.size() : 0;
    }

    public static <T> int len(T[] value) {
        return value != null ? value.length : 0;
    }

    public static int len(boolean[] value) {
        return value != null ? value.length : 0;
    }

    public static int len(byte[] value) {
        return value != null ? value.length : 0;
    }

    public static int len(short[] value) {
        return value != null ? value.length : 0;
    }

    public static int len(int[] value) {
        return value != null ? value.length : 0;
    }

    public static int len(long[] value) {
        return value != null ? value.length : 0;
    }

    public static int len(float[] value) {
        return value != null ? value.length : 0;
    }

    public static int len(double[] value) {
        return value != null ? value.length : 0;
    }

    // also, please note that this method unlike others returns LONG
    public static long len(File value) {
        final long out;
        if (value != null && value.exists()) {
            // recursively count it's children if it's a directory
            if (value.isDirectory()) {
                final File[] children = value.listFiles();
                if (len(children) > 0) {
                    long l = 0L;
                    // `children` cannot be null here
                    //noinspection ConstantConditions
                    for (File child: children) {
                        l += len(child);
                    }
                    out = l;
                } else {
                    out = 0L;
                }
            } else {
                out = value.length();
            }
        } else {
            out = 0L;
        }
        return out;
    }

    public static int len(Object value) {
        return value == null ? 0 : 1;
    }

    // private constructor, this class is not intended to be initialized
    private Length() {}
}
