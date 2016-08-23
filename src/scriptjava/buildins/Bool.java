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

public class Bool {

    // as in docs, called without arguments returns false
//    public static boolean bool() {
//        return false;
//    }

    public static <T extends CharSequence> boolean bool(T value) {
        return value != null && value.length() > 0;
    }

    public static <T extends Collection<?>> boolean bool(T value) {
        return value != null && value.size() > 0;
    }

    public static <T extends Map<?, ?>> boolean bool(T value) {
        return value != null && value.size() > 0;
    }

    public static <T> boolean bool(T[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(boolean value) {
        return value;
    }

    public static boolean bool(Boolean value) {
        return value != null && value;
    }

    public static boolean bool(byte value) {
        return value != 0;
    }

    public static boolean bool(Byte value) {
        return value != null && value != 0;
    }

    public static boolean bool(short value) {
        return value != 0;
    }

    public static boolean bool(Short value) {
        return value != null && value != 0;
    }

    public static boolean bool(int value) {
        return value != 0;
    }

    public static boolean bool(Integer value) {
        return value != null && value != 0;
    }

    public static boolean bool(long value) {
        return value != 0L;
    }

    public static boolean bool(Long value) {
        return value != null && value != 0L;
    }

    public static boolean bool(float value) {
        return Float.compare(value, .0F) != 0;
    }

    public static boolean bool(Float value) {
        return value != null && Float.compare(value, .0F) != 0;
    }

    public static boolean bool(double value) {
        return Double.compare(value, .0D) != 0;
    }

    public static boolean bool(Double value) {
        return value != null && Double.compare(value, .0D) != 0;
    }

    public static boolean bool(boolean[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(byte[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(short[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(int[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(long[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(float[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(double[] value) {
        return value != null && value.length > 0;
    }

    public static boolean bool(File value) {
        return value != null && value.exists();
    }

    public static boolean bool(Object value) {
        return value != null;
    }

    private Bool() {}

}
