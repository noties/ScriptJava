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

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;
import static scriptjava.buildins.Range.range;

public class RangeTest {

    private static final int[] EMPTY = new int[0];

    @Test
    public void testRangeEmpty() throws Exception {
        assertRange(EMPTY, 0);
        assertRange(EMPTY, 0, 0);
        assertRange(EMPTY, 0, 0, 0);
        assertRange(EMPTY, 0, -1, 1);
        assertRange(EMPTY, -1, -1, -1);
        assertRange(EMPTY, 0, 100, 0); // zero step == endless
        assertRange(EMPTY, -1, -5, 1); // endless
        assertRange(EMPTY, -1);
    }

    @Test
    public void testRange1() throws Exception {
        assertRange(array(0), 1);
        assertRange(array(0, 1, 2, 3), 4);
        assertRange(array(0, -1, -2, -3, -4), 0, -5, -1);
    }

    private static int[] array(int... values) {
        return values;
    }

    private static void assertRange(int[] expected, int end) {
        assertRange(String.valueOf(end), expected, range(end));
    }

    private static void assertRange(int[] expected, int start, int end) {
        assertRange("" + start + ", " + end, expected, range(start, end));
    }

    private static void assertRange(int[] expected, int start, int end, int step) {
        assertRange("" + start + ", " + end + ", " + step, expected, range(start, end, step));
    }

    private static void assertRange(String message, int[] expected, int[] actual) {
        assertTrue(message + ": " + Str.str(expected) + " / " + Str.str(actual), Arrays.equals(expected, actual));
    }
}