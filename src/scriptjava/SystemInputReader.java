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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

class SystemInputReader {

    interface OnNewLineListener {
        void onNewLine(String line);
    }

    private volatile boolean isRunning;

    SystemInputReader() {

    }

    void start(OnNewLineListener lineListener) {

        isRunning = true;

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        String line;

        try {
            while (isRunning) {
                line = reader.readLine();
                if (line != null && line.length() > 0) {
                    lineListener.onNewLine(line);
                }
            }
        } catch (IOException e) {
            // it's a bad thing, but we don't expect it here
        }

        // ok, we have finished our listening process -> release the reader
        try {
            reader.close();
        } catch (IOException e) {
            // as we are finishing our process, we don't handle this exception
        }
    }

    void stop() {
        isRunning = false;
    }
}
