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

import java.io.*;

public class ProcessRedirect {

    public interface Callback {
        void apply();
    }

    public static void redirect(InputStream inputStream, Callback callback) {
        redirect(System.out, inputStream, callback);
    }

    public static void redirect(PrintStream printStream, InputStream inputStream, Callback callback) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                printStream.println(line);
                if (callback != null) {
                    callback.apply();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // nothing we can do
                e.printStackTrace();
            }
        }
    }
}
