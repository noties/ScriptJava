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

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

class ScriptClassLoader {

    static class CannotCreateInstanceException extends Exception {
        public CannotCreateInstanceException(Throwable cause) {
            super(cause);
        }
    }

    private final File scriptFolder;

    ScriptClassLoader(File scriptFolder) {
        this.scriptFolder = scriptFolder;
    }

    private static URLClassLoader obtain(File folder) {
        try {
            return URLClassLoader.newInstance(new URL[] { folder.toURI().toURL() }, ScriptClassLoader.class.getClassLoader());
        } catch (Throwable t) {
            throw new RuntimeException("Cannot create class loader", t);
        }
    }

    <T> T createInstance(String name) throws CannotCreateInstanceException {
        try {

            final Class<?> cl = Class.forName(name, true, obtain(scriptFolder));
            //noinspection unchecked
            return (T) cl.newInstance();

        } catch (Throwable t) {
            // this is very interesting because we may be not able to create a class because of a script error
            // terminating all the process would be a real pain
            throw new CannotCreateInstanceException(t);
        }
    }
}
