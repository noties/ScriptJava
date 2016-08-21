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
import scriptjava.buildins.Buildins;
import scriptjava.buildins.IO;

import java.io.File;
import java.util.Locale;

public class Main {

    private static final String HOME_PROPERTY = "SCRIPTJAVA_HOME";

    public static void main(String[] args) {

        // pyjama

        // start with a greeting message
        System.out.println(welcomeMessage());

        final File executionFolder;
        final File scriptFolder;
        {
            executionFolder = executionFolder();
            if (!Bool.bool(executionFolder)) {
                if (!executionFolder.mkdirs()) {
                    throw new RuntimeException("Cannot create execution folder at path: " + executionFolder.getAbsolutePath());
                }
            }
            // we use the current execution folder to store our temporary folder
            scriptFolder = new File(executionFolder, ".scriptjava");

            // if the folder exists at this point this means there was some error
            // finishing the app the previous time, let's delete the folder first
            if (Bool.bool(scriptFolder)) {
                IO.del(scriptFolder);
            }

            if (!scriptFolder.exists() && !scriptFolder.mkdirs()) {
                throw new RuntimeException("Could not create script folder: " + scriptFolder.getAbsolutePath());
            }

            // let's clear the contents of our created temporary folder
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    // let's check if our folder is not empty & clear it if it is
                    IO.del(scriptFolder);
                }
            }));
        }

	    // OK, what we do here is listening for input events and redirect them to our class builder
        final SystemInputReader inputReader = new SystemInputReader();
        final ScriptWriter writer = new ScriptWriter(executionFolder, scriptFolder, new ScriptWriter.OnTerminationListener() {
            @Override
            public void terminate() {
                inputReader.stop();
                System.exit(0);
            }
        });
        writer.prepare();
        inputReader.start(writer);
    }

    private static String welcomeMessage() {
        final String userName = System.getProperty("user.name", "Stranger");
        final String javaVendor;
        final String javaVersion;
        final String javaTitle;
        {
            final Package pkg = Runtime.class.getPackage();
            javaVendor  = pkg.getImplementationVendor();
            javaVersion = pkg.getImplementationVersion();
            javaTitle   = pkg.getImplementationTitle();
        }
        return String.format(Locale.US, "\nWelcome %s!\n[%s %s %s]\n", userName, javaTitle, javaVersion, javaVendor);
    }

    private static File executionFolder() {
        final File folder;
        final String home = System.getenv(HOME_PROPERTY);
        if (Bool.bool(home)) {
            folder = new File(home);
        } else {
            folder = new File(System.getProperty("user.dir"));
        }
        return folder;
    }
}
