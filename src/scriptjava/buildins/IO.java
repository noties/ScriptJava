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

import java.io.*;

public class IO {

    // todo, simpler API

    public static File file() {
        return new File(System.getProperty("user.dir"));
    }

    public static File file(File folder, String name) {
        return new File(folder, name);
    }

    public static File file(String path) {
        return new File(path);
    }

    public static File file(boolean createIfNotExists, String path) {

        final File file = file(path);

        if (!Bool.bool(file)) {
            if (!createIfNotExists) {
                return null;
            } else {
                try {

                    if (!file.createNewFile()) {
                        throw new RuntimeException("Unable to create a new file: " + path);
                    }

                    return file;

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            return null;
        } else {
            return file;
        }
    }

    public static boolean del(File file) {

        // it doesn't exist
        if (!Bool.bool(file)) {
            return true;
        }

        if (!file.isDirectory()) {
            if (!file.delete()) {
                throw new RuntimeException("Cannot delete file: " + file.getPath());
            }

            return true;
        }

        final File[] files = file.listFiles();
        if (Bool.bool(files)) {
            //noinspection ConstantConditions
            for (File f: files) {
                del(f);
            }
        }

        if (!file.delete()) {
            throw new RuntimeException("Cannot delete file: " + file.getPath());
        }

        return true;
    }

    public static boolean del(String path) {
        return del(new File(path));
    }

    public static boolean write(File file, String contents) {

//        System.out.printf("bool: %s, file: %s%n", Bool.bool(file), file.getPath());

        final boolean result;
        if (!Bool.bool(file)) {
            result = false;
        } else {

            boolean innerResult = false;

            BufferedWriter writer = null;
            try {

                if (file.delete()) {
                    if (!file.createNewFile()) {
                        throw new RuntimeException("Cannot create a file: " + file.getPath());
                    }
                } else {
                    throw new RuntimeException("Cannot delete file: " + file.getPath());
                }

                writer = new BufferedWriter(new FileWriter(file, false));
                writer.write(contents);

                innerResult = true;

            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        // nothing here
                    }
                }
            }

            result = innerResult;
        }
        return result;
    }

    public static boolean write(File file, byte[] bytes) {

        // if file is not exist -> we will create it
        // but if it's NULL we can do anything
        if (file == null) {
            return false;
        }

        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            if (!file.exists()) {
                return false;
            }
        }

        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file, false));
            outputStream.write(bytes);

            return true;

        } catch (Throwable t) {
            t.printStackTrace();

            return false;

        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {}
            }
        }
    }

    public static byte[] bytes(InputStream stream) {
        final byte[] out;
        if (!Bool.bool(stream)) {
            out = null;
        } else {
            byte[] bytes;
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final int length = 1024 * 8;
            final byte[] buf = new byte[length];
            int read;
            try {
                while ((read = stream.read(buf, 0, length)) > -1) {
                    baos.write(buf, 0, read);
                }
                baos.flush();
                bytes = baos.toByteArray();
            } catch (Throwable t) {
                t.printStackTrace();
                bytes = null;
            } finally {
                try {
                    stream.close();
                } catch (Throwable t) {}
            }
            out = bytes;
        }
        return out;
    }

    private IO() {

    }
}
