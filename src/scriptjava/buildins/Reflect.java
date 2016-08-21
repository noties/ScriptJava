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

package scriptjava.buildins;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Reflect {

    public static Map<String, ?> dict(Object o) {
        return dict(o, false);
    }

    public static Map<String, ?> dict(Object o, boolean includeMethods) {

        final Map<String, Object> map;

        // class, superclass, implements[], fields, methods
        if (o == null) {
            //noinspection unchecked
            map = Collections.EMPTY_MAP;
        } else {
            map = new HashMap<>();
            final Class<?> cl = o.getClass();

            // extract basic info about class
            {
                map.put("class", Str.str(cl));
                final Class<?> superCl = cl.getSuperclass();
                if (superCl != null && Object.class != superCl) {
                    map.put("superClass", Str.str(superCl));
                }
                final Class<?>[] interfaces = cl.getInterfaces();
                final int interfacesLength = Length.len(interfaces);
                if (interfacesLength > 0) {
                    final String[] array = new String[interfacesLength];
                    for (int i = 0; i < interfacesLength; i++) {
                        array[i] = Str.str(interfaces[i]);
                    }
                    map.put("implements", array);
                }
            }

            // let's just iterate over declaredFields
            {
                final Field[] fields = cl.getDeclaredFields();
                if (Bool.bool(fields)) {
                    final Map<String, Object> fieldsMap = new HashMap<>();
                    for (Field field: fields) {
                        field.setAccessible(true);
                        try {
                            fieldsMap.put(field.getName(), Str.str(field.get(o)));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                    map.put("fields", fieldsMap);
                }
            }

            // should we also add methods?
            {
                if (includeMethods) {
                    final Method[] methods = cl.getDeclaredMethods();
                    final int length = Length.len(methods);
                    if (length > 0) {
                        final String[] array = new String[length];
                        for (int i = 0; i < length; i++) {
                            array[i] = buildMethodSignature(methods[i]);
                        }
                        map.put("methods", array);
                    }
                }
            }
        }

        return map;
    }

    private static String buildMethodSignature(Method method) {
        // *$modifiers $return_type $name ($arguments) $throws
        final StringBuilder builder = new StringBuilder();

        {
            // modifiers
            final int modifiers = method.getModifiers();
            builder.append(Modifier.toString(modifiers))
                    .append(' ');
        }

        builder.append(Str.str(method.getReturnType()))
                .append(' ')
                .append(method.getName())
                .append("(");

        {
            final Class<?>[] args = method.getParameterTypes();
            final int length = Length.len(args);
            if (length > 0) {
                for (int i = 0; i < length; i++) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append(Str.str(args[i]));
                }
            }
        }

        builder.append(")");

        // check throws
        {
            final Class<?>[] exceptions = method.getExceptionTypes();
            final int length = Length.len(exceptions);
            if (length > 0) {
                builder.append(" throws ");
                for (int i = 0; i < length; i++) {
                    if (i != 0) {
                        builder.append(", ");
                    }
                    builder.append(Str.str(exceptions[i]));
                }
            }
        }

        return builder.toString();
    }

    public static String type(Object o) {
        if (o == null) {
            return "null";
        } else {
            return Str.str(o.getClass());
        }
    }

    // converts given object into a Map
    public static Map<String, Object> toMap(Object o) {

        if (o == null) {
            return null;
        }

        final Map<String, Object> map;
        final Class<?> cl = o.getClass();
        final Field[] fields = cl.getDeclaredFields();
        if (Bool.bool(fields)) {
            map = new HashMap<>();
            for (Field f: fields) {
                // static fields?
                if (!Modifier.isStatic(f.getModifiers())) {
                    f.setAccessible(true);
                    try {
                        map.put(f.getName(), f.get(o));
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        } else {
            //noinspection unchecked
            map = Collections.EMPTY_MAP;
        }

        return map;
    }

    // creates an object from map, trying to match fields
    public static <T> T fromMap(Class<T> cl, Map<String, Object> map) {
        final T out;
        if (!Bool.bool(map) || !Bool.bool(cl)) {
            out = null;
        } else {
            out = ni(cl);
            if (Bool.bool(out)) {
                Field field;
                for (Map.Entry<String, Object> entry: map.entrySet()) {
                    try {
                        field = cl.getDeclaredField(entry.getKey());
                        if (Bool.bool(field) && !Modifier.isFinal(field.getModifiers())) {
                            field.setAccessible(true);
                            field.set(out, entry.getValue());
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }
        return out;
    }

    // sets the value for the field with name (if present)
    // possible to change final fields
    public static boolean set(Object o, String name, Object value) {

        if (o == null) {
            return false;
        }

        final Class<?> cl = o.getClass();
        try {
            final Field field = cl.getDeclaredField(name);
            if (field != null) {
                if (Modifier.isFinal(field.getModifiers())) {
                    final Class<?> fieldClass = Field.class;
                    final Field modifiersField = fieldClass.getDeclaredField("modifiers");
                    modifiersField.setAccessible(true);
                    modifiersField.set(field, field.getModifiers() & ~Modifier.FINAL);
                }
                field.setAccessible(true);
                field.set(o, value);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }

        return true;
    }

    public static <T> T ni(Class<T> cl) {
        // unsafe? create without constructor?
        try {
            return cl.newInstance();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    private Reflect() {

    }
}
