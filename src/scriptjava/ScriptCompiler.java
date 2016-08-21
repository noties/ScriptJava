/* Copyright 2016 Dimitry Ivanov (cr@dimitryivanov.ru)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package scriptjava;

import scriptjava.buildins.Bool;

import java.io.*;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

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

    private final File executionFolder;
    private final File scriptFolder;
    private final File libsFolder;

    ScriptCompiler(File executionFolder, File scriptFolder) {
        this.executionFolder = executionFolder;
        this.scriptFolder = scriptFolder;
        this.libsFolder = libsFolder(executionFolder);

        // the thing is -> it's not enough to compile the Script with included class path
        // we need to provide all these libs (if present) at runtime, thus we need
        // to add custom path to our class loader
        if (libsFolder != null) {
            addLibsFolderToClassLoader(libsFolder);
        }
    }

    private static void addLibsFolderToClassLoader(File libsFolder) {

        final File[] files = libsFolder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        if (!Bool.bool(files)) {
            return;
        }

        final ClassLoader classLoader = ScriptCompiler.class.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            try {

                final URLClassLoader urlClassLoader = (URLClassLoader) classLoader;
                final Method addPath = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addPath.setAccessible(true);

                for (File file: files) {
                    addPath.invoke(urlClassLoader, file.toURI().toURL());
                }

                return;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        System.out.printf("Unable to modify classLoader to add libraries from: %s%n", libsFolder.getPath());
    }

    private static File libsFolder(File executionFolder) {
        final File libs = new File(executionFolder, "libs");
        if (Bool.bool(libs)) {
            return libs;
        } else {
            return null;
        }
    }

    boolean compile(String name, String source) {

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
        write(file, source);

        final Value<Boolean> result = new ValueMutable<>(Boolean.TRUE);

        // at first I was using javax.tools.JavaCompiler
        // but from commandLine it falls with NULL compiler..
        {
            final Runtime runtime = Runtime.getRuntime();
            Process process = null;
            try {
                process = runtime.exec(String.format(COMPILE_STATEMENT, classpath(), file.getPath()));
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

    private String classpath() {

        final StringBuilder builder = new StringBuilder();
        builder.append('\"')
                .append(executionFolder.getPath())
                .append(File.separator)
                .append('*');

        // additionally add an optional `libs` folder inside the execution folder
        if (libsFolder != null) {
            builder.append(CLASSPATH_SEPARATOR)
                    .append(libsFolder.getPath())
                    .append(File.separator)
                    .append('*');
        }

        builder.append(CLASSPATH_SEPARATOR)
                .append('\"');

        return builder.toString();
    }

    private static void write(File file, String data) {
        Writer writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(data);
        } catch (Throwable t) {
            throw new RuntimeException("Cannot write string data to a file: " + file.getAbsolutePath(), t);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    // cannot do anything
                }
            }
        }
    }
}
