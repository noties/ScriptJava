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

package scriptjava;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ScriptDependenciesTest {

    @Test
    public void testReader() {
        assertReader("", null, null);
        assertReader(null, null, null);
        assertReader("ScriptJava.jar", null, new ScriptDependencies.FileData("ScriptJava.jar"));
        assertReader("mv/", null, new ScriptDependencies.FileData("mv/"));
        assertReader("\"some folder\\some inner folder\\\"", null, new ScriptDependencies.FileData("some folder\\some inner folder\\"));
        assertReader(
                "-o \'output folder\' \'some:some:1.2.3\'",
                new ScriptDependencies.Arguments("output folder"),
                new ScriptDependencies.MavenData("some", "some", "1.2.3")
        );
        assertReader(
                "-o mv some:some:+",
                new ScriptDependencies.Arguments("mv"),
                new ScriptDependencies.MavenData("some", "some", "+")
        );
    }

    @Test
    public void testReaderParametersParsing() {
        assertParameters("");
        assertParameters(null);
        assertParameters(" ");
        assertParameters(" \"\' ");
        assertParameters("\"\"\"\"\"\"");
        assertParameters("\'\'\'\'\'\'");
        assertParameters("\"\"\'\'");
        assertParameters("ScripJava.jar", "ScripJava.jar");
        assertParameters("-o folder/ ScriptJava.jar", "-o", "folder/", "ScriptJava.jar");
        assertParameters("    -o      folder/             ScriptJava.jar                ", "-o", "folder/", "ScriptJava.jar");
        assertParameters("\'-o\' \"folder/another with spaces/\" \'ScriptJava .jar\'", "-o", "folder/another with spaces/", "ScriptJava .jar");
    }

    private static void assertReader(
            final String what,
            final ScriptDependencies.Arguments expectedArguments,
            final ScriptDependencies.Data expectedData
    ) {
        final ScriptDependencies.Reader reader = new ScriptDependencies.Reader(new ScriptDependencies.Reader.Callbacks() {
            @Override
            public void onComplete(ScriptDependencies.Arguments arguments, ScriptDependencies.Data data) {
                assertEquals(what, expectedArguments, arguments);
                assertEquals(what, expectedData, data);
            }
        });
        reader.read(what);
    }

    private static void assertParameters(String text, String... parts) {
        assertEquals(text, Arrays.asList(parts), ScriptDependencies.Reader.parseParameters(text));
    }
}