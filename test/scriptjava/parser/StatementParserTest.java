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
import scriptjava.ScriptWriterHelper;
import scriptjava.buildins.Length;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StatementParserTest {

    // parser has no inner state
    private final StatementParser parser = new StatementParserManual(ScriptWriterHelper.buildIns());

    @Test
    public void testInit() {
        check(new StatementMember(null), "{");
    }

    @Test
    public void testStaticInit() {
        check(new StatementMember(null), "static {");
    }

    @Test
    public void testIf() {

        final String[] array = array(
                "if (true) {",
                "if (s == null) print(false);",
                "if (some != null) {} else {}",
                "if (i == 0) print(false); else print(true);",
                "if (bool(null)) {"
        );

        check(new StatementExecution(null, false), array);
    }

    @Test
    public void testFor() {

        final String[] array = array(
                "for (int i = 0; i < 10; i++) {",
                "for (String s: someArray) {",
                "for (int i: someArray) {}",
                "for (int i = 0; i < 5; i++) print(i);",
                "for (;;) {}"
        );

        check(new StatementExecution(null, false), array);
    }

    @Test
    public void testWhile() {

        final String[] array = array(
                "while (true) {}",
                "while (i != 0) {",
                "while (false) print(false!);",
                "while (false)"
        );

        check(new StatementExecution(null, false), array);
    }

    @Test
    public void testDo() {

        final String[] array = array(
                "do print(false) while(true)",
                "do {"
        );

        check(new StatementExecution(null, false), array);
    }

    @Test
    public void testMethod() {

        final String[] array = array(
                "public void method() {",
                "private static final String method() {",
                "static List<String> method() {",
                "private final Map<K, V> toMap(boolean arg1) {",
                "void nada() {}"
        );

        check(new StatementMember(null), array);
    }

    @Test
    public void testClass() {

        final String[] array = array(
                "public static class C {",
                "class C extends B {",
                "private class C implements A, B {",
                "final class C<T> {",
                "public abstract class C {"
        );

        check(new StatementMember(null), array);
    }

    @Test
    public void testMemberVariable() {

        final String[] array = array(
                "private int i;",
                "private final long l = 0L;",
                "public static final String CONST = \"CONST\"",
                "protected double d = .5D;",
                "private final Date d = new Date() {",
                "private final Date d = new Date() { public String toString() { return null; } }"
        );

        check(new StatementMember(null), array);
    }

    @Test
    public void testExecutionVariable() {

        final String[] array = array(
                "int i = 0;",
                "long l = 15;",
                "float f = getFloat(33.F);",
                "final String s = null;",
                "final List<String> list = list(null, null, \"1\");",
                "final Map<K, V> map = map(new String[] { null, null }, new Object[] { null, null });",
                "final Date d = new Date() { public String toString() { return null; } };"
        );

        check(new StatementExecution(null, false), array);
    }

    @Test
    public void testNewValueForVariable() {

        final String[] array = array(
                "i = 0;",
                "f = getFloat(122);",
                "some = null;",
                "h = 1 + 1;",
                "list = list(1, 2, 3)",
                "map = map(new String[] { null, null }, new Object[] { null, null })"
        );

        check(new StatementExecution(null, false), array);
    }

    @Test
    public void testVoidCall() {

        final String[] array = array(
                "some.call();",
                "call();",
                "MySuperInstance.getThisValuePlease(l1, l2, l3, list(1, 2, 3));",
                "new Date().toString()"
        );

        check(new StatementExecution(null, false), array);
    }

    @Test
    public void testBuildInCall() {

        final class Make {
            String make(String in, String args) {
                if (Length.len(args) == 0) {
                    args = "123";
                }
                return String.format("%1$s(%2$s)", in, args);
            }
        }
        final Make make = new Make();

        final Set<String> strings = StatementParserBase.buildIns(ScriptWriterHelper.buildIns());
        // the `print` statement itself is not detected as isPrint...remove it from iteration
        strings.remove("print");
        strings.remove("printf");
        final int length = Length.len(strings);
        final String[] array = new String[length * 3];
        int i = 0;
        for (String s: strings) {
            array[i++] = make.make(s, "someValue()");
            array[i++] = make.make(s, null);
            array[i++] = make.make(s, "new String[] { null }");
        }

        check(new StatementExecution(null, true), array);
    }

    @Test
    public void testPrintStatement() {

        final String[] array = array(
                "1 + 1",
                "something * thisThing",
                "this",
                "some typed chars",
                "7 & 7",
                "1 >>> 1",
                "2 << 0",
                "true == false",
                "true != true",
                "Integer.MAX_VALUE",
                "Integer.MAX_VALUE + Long.MAX_VALUE",
                "null",
                "Script.class",
                "(this != null) && (this == this)"
        );

        check(new StatementExecution(null, true), array);
    }

    private static String[] array(String... ins) {
        return ins;
    }

    private void check(Statement expected, String... ins) {
        for (String in: ins) {
            final Statement statement = parser.parse(in);
            assertEquals(in, expected.type(), statement.type());
            assertEquals(in, expected.isPrintStatement(), statement.isPrintStatement());
        }
    }
}