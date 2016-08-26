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

import scriptjava.buildins.Bool;
import scriptjava.buildins.IO;

import java.io.File;
import java.util.List;

class ScriptCompiler {

    private static final String CLASSPATH_SEPARATOR;
    private static final String COMPILE_STATEMENT;
    static {
        final String sep;
        if (System.getProperty("os.name").startsWith("Windows")) {
            sep = ";";
        } else {
            sep = ":";
        }
        CLASSPATH_SEPARATOR = sep;
        COMPILE_STATEMENT = "javac -cp %1$s -nowarn -Xlint:none %2$s"; // 1-classpath; 2-file
    }

    private final File scriptFolder;

    ScriptCompiler(File scriptFolder) {
        this.scriptFolder = scriptFolder;
    }

    boolean compile(String name, String source, List<String> dependencies) {

        final File file = new File(scriptFolder, name);
        try {
            // weird stuff... look at return value AND catch the throwable
            if (!file.exists() && !file.createNewFile()) {
                throw null;
            }
        } catch (Throwable t) {
            throw new RuntimeException("Cannot create a file: " + file.getAbsolutePath(), t);
        }

        // we write the source to this file
        IO.write(file, source);

        final Value<Boolean> result = new ValueMutable<>(Boolean.TRUE);

        // at first I was using javax.tools.JavaCompiler
        // but from commandLine it falls with NULL compiler..
        {
            final Runtime runtime = Runtime.getRuntime();
            Process process = null;
            try {
                process = runtime.exec(String.format(COMPILE_STATEMENT, classpath(dependencies), file.getPath()));
                ProcessRedirect.redirect(process.getInputStream(), null);
                ProcessRedirect.redirect(process.getErrorStream(), new ProcessRedirect.Callback() {
                    @Override
                    public void apply() {
                        result.set(Boolean.FALSE);
                    }
                });
                // sync call
                process.waitFor();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                result.set(Boolean.FALSE);
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        }

        return result.get();
    }

    private String classpath(List<String> dependencies) {

        if (!Bool.bool(dependencies)) {
            return null;
        }

        final StringBuilder builder = new StringBuilder()
                .append('\"');

        boolean first = true;
        for (String dep: dependencies) {
            if (first) {
                first = false;
            } else {
                builder.append(CLASSPATH_SEPARATOR);
            }
            builder.append(dep);
        }

        builder.append(CLASSPATH_SEPARATOR)
                .append('\"');

        return builder.toString();
    }
}
