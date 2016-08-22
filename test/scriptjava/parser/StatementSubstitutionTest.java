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

package scriptjava.parser;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class StatementSubstitutionTest {

    @Test
    public void testSubstitute() {
        check("map(key1: 1, key2: 2)", "map(new String[] { \"key1\", \"key2\" }, new Object[] { 1, 2 })");
    }

    @Test
    public void testNoSubstitute() {
        check("hello", "hello");
        check("Map map = new HashMap()", "Map map = new HashMap()");
    }

    private void check(String in, String expected) {
        assertEquals(String.format(Locale.US,"in: %s, expected: %s", in, expected), expected, StatementSubstitution.substitute(in));
    }
}