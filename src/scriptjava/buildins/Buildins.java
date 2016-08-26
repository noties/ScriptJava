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

import java.net.URLEncoder;
import java.util.*;

public class Buildins {

    public static void print(Object value, Object... others) {

        System.out.print(Str.str(value));

        if (Bool.bool(others)) {
            for (Object o: others) {
                System.out.print(", ");
                System.out.print(Str.str(o));
            }
        }

        System.out.println();
    }

    public static void printf(String message, Object... args) {
        System.out.printf(message, args);
        System.out.println();
    }

    // URL encode
    public static String enc(String in) {
        final String out;
        if (Bool.bool(in)) {
            String encoded;
            try {
                encoded = URLEncoder.encode(in, "UTF-8");
            } catch (Throwable t) {
                t.printStackTrace();
                encoded = null;
            }
            out = encoded;
        } else {
            out = in;
        }
        return out;
    }

    public static String bin(long value) {
        return "0b" + Long.toString(value, 2);
    }

    public static String hex(long value) {
        return "0x" + Long.toHexString(value);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static Date date() {
        return new Date();
    }

    public static <T> List<T> list(T... values) {
        if (Length.len(values) == 0) {
            //noinspection unchecked
            return Collections.EMPTY_LIST;
        } else {
            return Arrays.asList(values);
        }
    }

    public static <K, V> Map<K, V> map(K[] keys, V[] values) {

        final int length;
        {
            length = Length.len(keys);

            final int valuesLength  = Length.len(values);

            if (length != valuesLength) {
                throw new IllegalArgumentException("Supplied arrays must have exactly the same length");
            }
        }

        if (length == 0) {
            //noinspection unchecked
            return Collections.EMPTY_MAP;
        }

        final Map<K, V> map = new HashMap<>();

        for (int i = 0; i < length; i++) {
            map.put(keys[i], values[i]);
        }

        return map;
    }
}
