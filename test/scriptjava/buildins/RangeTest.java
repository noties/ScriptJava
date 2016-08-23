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

import static org.junit.Assert.*;
import static scriptjava.buildins.Range.range;

/**
 * Created by Дмитрий on 23.08.2016.
 */
public class RangeTest {

    @Test
    public void testRangeEmpty() throws Exception {
        assertRange("0", new int[0], range(0));
        assertRange("0, 0", new int[0], range(0, 0));
        assertRange("0, 0, 0", new int[0], range(0, 0, 0));
        assertRange("0, -1, 1", new int[0], range(0, -1, 1));
        assertRange("-1, -1, -1", new int[0], range(-1, -1, -1));
        assertRange("0, 100, 0", new int[0], range(0, 100, 0)); // zero step == endless
        assertRange("-1, -5, 1", new int[0], range(-1, -5, 1)); // endless
        assertRange("-1", new int[0], range(-1));
    }

    @Test
    public void testRange1() throws Exception {
        assertRange("1", new int[] { 0 }, range(1));
        assertRange("4", array(0, 1, 2, 3), range(4));
        assertRange("0, -5, -1", array(0, -1, -2, -3, -4), range(0, -5, -1));
    }

    private static int[] array(int... values) {
        return values;
    }

    private static void assertRange(String suppliedValues, int[] expected, int[] actual) {
        assertTrue(suppliedValues + ": " + Str.str(expected) + " / " + Str.str(actual), Arrays.equals(expected, actual));
    }
}