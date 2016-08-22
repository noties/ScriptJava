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
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;

public class Str {

    public static String str(Object value) {
        final String out;
        // if it's null, it's a `null`
        if (value == null) {
            out = "null";
        } else {
            // ok, check if it's an array
            final Class<?> cl = value.getClass();
            if (cl.isArray()) {
                out = strArray(value);
            } else if (value instanceof Collection<?>) {
                out = strCollection(value);
            } else if (value instanceof Map<?, ?>) {
                out = strMap(value);
            } else if (value instanceof Class<?>) {
                out = strClass(value);
            } else if(value instanceof File) {
                out = strFile(value);
            } else if(value instanceof InputStream) {
                out = strInputStream(value);
            } else {
                out = String.valueOf(value);
            }
        }
        return out;
    }

    private static String strArray(Object value) {
        final int length = Array.getLength(value);
        final StringBuilder builder = new StringBuilder();
        // append type info
        builder.append(strClass(value.getClass()))
                .append('{')
                .append(' ');
        for (int i = 0; i < length; i++) {
            if (i != 0) {
                builder.append(", ");
            }
            builder.append(str(Array.get(value, i)));
        }
        builder.append(' ')
                .append('}');
        return builder.toString();
    }

    private static String strCollection(Object value) {
        final Collection<?> collection = (Collection<?>) value;
        final StringBuilder builder = new StringBuilder();
        builder.append(str(value.getClass()))
                .append('{')
                .append(' ');
        boolean first = true;
        for (Object o: collection) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(str(o));
        }
        builder.append(' ')
                .append('}');
        return builder.toString();
    }

    private static String strMap(Object value) {
        final Map<?, ?> map = (Map<?, ?>) value;
        final StringBuilder builder = new StringBuilder();
        builder.append(strClass(value.getClass()))
                .append("{ ");
        boolean first = true;
        for (Map.Entry<?, ?> entry: map.entrySet()) {
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append(str(entry.getKey()))
                    .append(": ")
                    .append(str(entry.getValue()));
        }
        builder.append(" }");
        return builder.toString();
    }

    private static String strClass(Object value) {

        final String out;

        final Class<?> valueClass = (Class<?>) value;
        if (valueClass.isArray()) {
            int dimensions = 1;
            Class<?> current = valueClass.getComponentType();
            while (current.isArray()) {
                dimensions += 1;
                current = current.getComponentType();
            }
            final String dimensionString;
            {
                if (dimensions == 1) {
                    dimensionString = "[]";
                } else {
                    final StringBuilder builder = new StringBuilder(dimensions * 2);
                    for (int i = 0; i < dimensions; i++) {
                        builder.append('[')
                                .append(']');
                    }
                    dimensionString = builder.toString();
                }
            }

            out = className(current) + dimensionString;
        } else {
            out = className(valueClass);
        }

        return out;
    }

    private static String className(Class<?> cl) {

        final String out;

        final String typeParameters;
        {

            final Type type = cl.getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                final Type[] types = ((ParameterizedType) type).getActualTypeArguments();
                final int length = Length.len(types);
                if (length == 1) {
                    typeParameters = types[0].getTypeName();
                } else if (length > 0) {
                    final StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < length; i++) {
                        if (i != 0) {
                            builder.append(", ");
                        }
                        builder.append(types[i].getTypeName());
                    }
                    typeParameters = builder.toString();
                } else {
                    typeParameters = null;
                }
            } else {
                typeParameters = null;
            }
        }

        final String name = cl.getName();

        if (Bool.bool(typeParameters)) {
            out = name + "<" + typeParameters + ">";
        } else {
            out = name;
        }

        return out;
    }

    private static String strFile(Object value) {
        final File file = (File) value;
        if (!Bool.bool(file)) {
            return null;
        }

        final String out;
        if (file.isDirectory()) {
            out = file.getAbsolutePath();
        } else {
            final StringBuilder builder = new StringBuilder();
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line)
                            .append('\n');
                }
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    // nothing here
                }
            }

            out = builder.toString();
        }

        return out;
    }

    private static String strInputStream(Object value) {
        final InputStream stream = (InputStream) value;
        final Scanner scanner = new Scanner(stream).useDelimiter("\\A");
        try {
            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            scanner.close();
            try {
                stream.close();
            } catch (IOException e) {
                // nothing here
            }
        }

        return null;
    }

    private Str() {

    }
}
