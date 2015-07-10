package com.company;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 10.07.2015.
 */
class _Methods {

    static final String PRINT = "static String print(Object o){return String.valueOf(o);}";
    static final String PRINT_F = "static String printf(String s, Object... a){return String.format(s, a);}";
    static final String TYPE_OF = "static String typeof(Object o){if (o == null) {return null;} return ClassString.toString(o.getClass());}";

    static final String CLASS_STRING = "" +
            "    private static class ClassString {\n" +
            "        static String toString(Class<?>[] classes) {\n" +
            "            if (classes == null\n" +
            "                    || classes.length == 0) {\n" +
            "                return \"\";\n" +
            "            }\n" +
            "            final StringBuilder builder = new StringBuilder();\n" +
            "            boolean isFirst = true;\n" +
            "            for (Class<?> c: classes) {\n" +
            "                if (!isFirst) {\n" +
            "                    builder.append(\", \");\n" +
            "                } else {\n" +
            "                    isFirst = false;\n" +
            "                }\n" +
            "                if (c.isArray()) {\n" +
            "                    builder.append(c.getComponentType().getName())\n" +
            "                            .append(\"[]\");\n" +
            "                } else {\n" +
            "                    builder.append(c.getName());\n" +
            "                }\n" +
            "            }\n" +
            "            return builder.toString();\n" +
            "        }\n" +
            "        static String toString(Class<?> c) {\n" +
            "            if (c.isArray()) {\n" +
            "                return c.getComponentType().getName() + \"[]\";\n" +
            "            }\n" +
            "            return c.getName();\n" +
            "        }\n" +
            "    }";

    static final String METHODS = "" +
            "static String methods(Object o) throws Throwable {\n" +
            "    if (o == null) return \"null\";\n" +
            "    final class Indent {\n" +
            "        int count = 0;\n" +
            "        public String toString() {\n" +
            "            if (count == 0) {\n" +
            "                return \"\";\n" +
            "            }\n" +
            "            char[] chars = new char[4 * count];\n" +
            "            Arrays.fill(chars, ' ');\n" +
            "            return new String(chars);\n" +
            "        }\n" +
            "    }\n" +
            "    Indent indent = new Indent();\n" +
            "    Class<?> c = (o instanceof Class) ? (Class<?>) o : o.getClass();\n" +
            "    Class<?>[] interfaces = c.getInterfaces();\n" +
            "    final StringBuilder builder = new StringBuilder()\n" +
            "            .append(Modifier.toString(c.getModifiers()))\n" +
            "            .append(' ')\n" +
            "            .append(c.getName());\n" +
            "    if (interfaces != null\n" +
            "            && interfaces.length > 0) {\n" +
            "        boolean isInterfacesFirst = true;\n" +
            "        for (Class<?> i: interfaces) {\n" +
            "            if (!isInterfacesFirst) {\n" +
            "                builder.append(\", \");\n" +
            "            } else {\n" +
            "                builder.append(\" implements \");\n" +
            "                isInterfacesFirst = false;\n" +
            "            }\n" +
            "            builder.append(i.getName());\n" +
            "        }\n" +
            "    }\n" +
            "    builder.append(\" {\\n \");\n" +
            "    indent.count = 1;\n" +
            "    boolean isFirst = true;\n" +
            "    while (true) {\n" +
            "        Method[] dm = c.getDeclaredMethods();\n" +
            "        if (!isFirst) {\n" +
            "            indent.count = indent.count + 1;\n" +
            "            builder.append(\"\\n \\n\")\n" +
            "                    .append(indent)\n" +
            "                    .append(\"from \")\n" +
            "                    .append(c.getName())\n" +
            "                    .append(\": {\");\n" +
            "            indent.count = indent.count + 1;\n" +
            "        }\n" +
            "        for (Method m: dm) {\n" +
            "            m.setAccessible(true);\n" +
            "            builder.append('\\n')\n" +
            "                    .append(indent)\n" +
            "                    .append(Modifier.toString(m.getModifiers()))\n" +
            "                    .append(' ')\n" +
            "                    .append(ClassString.toString(m.getReturnType()))\n" +
            "                    .append(' ')\n" +
            "                    .append(m.getDeclaringClass().getName())\n" +
            "                    .append('.')\n" +
            "                    .append(m.getName())\n" +
            "                    .append('(')\n" +
            "                    .append(ClassString.toString(m.getParameterTypes()))\n" +
            "                    .append(')');\n" +
            "            Class<?>[] ets = m.getExceptionTypes();\n" +
            "            boolean isETSFirst = true;\n" +
            "            if (ets != null\n" +
            "                    && ets.length > 0) {\n" +
            "                builder.append(\" throws \");\n" +
            "                for (Class<?> et: ets) {\n" +
            "                    if (!isETSFirst) {\n" +
            "                        builder.append(\", \");\n" +
            "                    } else {\n" +
            "                        isETSFirst = false;\n" +
            "                    }\n" +
            "                    builder.append(et.getName());\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        indent.count = indent.count - 1;\n" +
            "        if (isFirst) {\n" +
            "            isFirst = false;\n" +
            "        } else {\n" +
            "            builder.append('\\n')\n" +
            "                .append(indent)\n" +
            "                .append(\"}\");\n" +
            "            indent.count = indent.count - 1;\n" +
            "        }\n" +
            "\n" +
            "        c = c.getSuperclass();\n" +
            "\n" +
            "        if (c == null) {\n" +
            "            break;\n" +
            "        }\n" +
            "    }\n" +
            "\n" +
            "    builder.append(\"\\n};\");\n" +
            "\n" +
            "    return builder.toString();\n" +
            "}";

    static final String DICT = "" +
            "static String dict(Object o) throws Throwable {\n" +
            "    if (o == null) return \"null\";\n" +
            "    final class Indent {\n" +
            "        int count = 0;\n" +
            "        public String toString() {\n" +
            "            if (count == 0) {\n" +
            "                return \"\";\n" +
            "            }\n" +
            "            char[] chars = new char[4 * count];\n" +
            "            Arrays.fill(chars, ' ');\n" +
            "            return new String(chars);\n" +
            "        }\n" +
            "    }\n" +
            "    Indent indent = new Indent();\n" +
            "    Class<?> c = o.getClass();\n" +
            "    StringBuilder builder = new StringBuilder()\n" +
            "            .append(Modifier.toString(c.getModifiers()))\n" +
            "            .append(' ')\n" +
            "            .append(ClassString.toString(c))\n" +
            "            .append(\" {\\n \");\n" +
            "    indent.count++;\n" +
            "    boolean isFirst = true;\n" +
            "    while (true) {\n" +
            "        if (!isFirst) {\n" +
            "            builder.append(\"\\n \\n\")\n" +
            "                    .append(indent)\n" +
            "                    .append(\"from class \")\n" +
            "                    .append(c.getName())\n" +
            "                    .append(\" {\");\n" +
            "            indent.count++;\n" +
            "        }\n" +
            "        Field[] fields = c.getDeclaredFields();\n" +
            "        if (fields != null\n" +
            "                && fields.length > 0) {\n" +
            "            for (Field field: fields) {\n" +
            "                field.setAccessible(true);\n" +
            "                builder.append(\" \\n\")\n" +
            "                        .append(indent)\n" +
            "                        .append(Modifier.toString(field.getModifiers()))\n" +
            "                        .append(' ')\n" +
            "                        .append(ClassString.toString(field.getType()))\n" +
            "                        .append(' ')\n" +
            "                        .append(field.getName())\n" +
            "                        .append(\" = \");\n" +
            "                Object obj = field.get(o);\n" +
            "                String v;\n" +
            "                if (obj == null) {\n" +
            "                    v = \"null\";\n" +
            "                } else if (obj.getClass().isArray()) {\n" +
            "                    Class<?> objC = obj.getClass();\n" +
            "                    Class<?> cT = objC.getComponentType();\n" +
            "                    if (cT.isPrimitive()) {\n" +
            "                        if (cT.equals(Byte.TYPE)) {\n" +
            "                            v = Arrays.toString((byte[]) obj);\n" +
            "                        } else if (cT.equals(Boolean.TYPE)) {\n" +
            "                            v = Arrays.toString((boolean[]) obj);\n" +
            "                        } else if (cT.equals(Character.TYPE)) {\n" +
            "                            v = Arrays.toString((char[]) obj);\n" +
            "                        } else if (cT.equals(Short.TYPE)) {\n" +
            "                            v = Arrays.toString((short[]) obj);\n" +
            "                        } else if (cT.equals(Integer.TYPE)) {\n" +
            "                            v = Arrays.toString((int[]) obj);\n" +
            "                        } else if (cT.equals(Long.TYPE)) {\n" +
            "                            v = Arrays.toString((long[]) obj);\n" +
            "                        } else if (cT.equals(Float.TYPE)) {\n" +
            "                            v = Arrays.toString((float[]) obj);\n" +
            "                        } else if (cT.equals(Double.TYPE)) {\n" +
            "                            v = Arrays.toString((double[]) obj);\n" +
            "                        } else {\n" +
            "                            v = \"void[]\";\n" +
            "                        }\n" +
            "                    } else {\n" +
            "                        v = Arrays.toString((Object[]) obj);\n" +
            "                    }\n" +
            "                } else {\n" +
            "                    v = String.valueOf(obj);\n" +
            "                }\n" +
            "                builder.append(v);\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        if (!isFirst) {\n" +
            "            indent.count--;\n" +
            "            builder.append('\\n')\n" +
            "                    .append(indent)\n" +
            "                    .append(\"}\\n \");\n" +
            "        } else {\n" +
            "            isFirst = false;\n" +
            "        }\n" +
            "\n" +
            "        c = c.getSuperclass();\n" +
            "        if (c == null) {\n" +
            "            break;\n" +
            "        }\n" +
            "    }\n" +
            "    indent.count = 0;\n" +
            "    builder.append(indent)\n" +
            "            .append(\"};\");\n" +
            "    return builder.toString();\n" +
            "}";

    static final String GET = "" +
            "static Object get(Object o, String name) throws Throwable {\n" +
            "    if (o == null) return null;\n" +
            "    Class<?> c = o.getClass();\n" +
            "    Field[] fields = c.getDeclaredFields();\n" +
            "    if (fields == null\n" +
            "            || fields.length == 0) {\n" +
            "        return null;\n" +
            "    }\n" +
            "    for (Field f: fields) {\n" +
            "        if (f.getName().equals(name)) {\n" +
            "            f.setAccessible(true);\n" +
            "            return f.get(o);\n" +
            "        }\n" +
            "    }\n" +
            "    return null;\n" +
            "}";

    static final String SET = "" +
            "static void set(Object o, String name, Object w) throws Throwable {\n" +
            "    if (o == null) return;\n" +
            "    Class<?> c = o.getClass();\n" +
            "    Field[] fields = c.getDeclaredFields();\n" +
            "    if (fields == null\n" +
            "            || fields.length == 0) {\n" +
            "        return;\n" +
            "    }\n" +
            "    for (Field f: fields) {\n" +
            "        if (f.getName().equals(name)) {\n" +
            "            f.setAccessible(true);\n" +
            "            int m = f.getModifiers();\n" +
            "            if (Modifier.isFinal(m)) {\n" +
            "                Field modField = Field.class.getDeclaredField(\"modifiers\");\n" +
            "                modField.setAccessible(true);\n" +
            "                modField.setInt(f, m & ~Modifier.FINAL);\n" +
            "            }\n" +
            "            f.set(o, w);\n" +
            "            return;\n" +
            "        }\n" +
            "    }\n" +
            "}";

//static class ClassString {
//    static String toString(Class<?>[] classes) {
//        if (classes == null
//                || classes.length == 0) {
//            return "";
//        }
//        final StringBuilder builder = new StringBuilder();
//        boolean isFirst = true;
//        for (Class<?> c: classes) {
//            if (!isFirst) {
//                builder.append(", ");
//            } else {
//                isFirst = false;
//            }
//            if (c.isArray()) {
//                builder.append(c.getComponentType().getName())
//                        .append("[]");
//            } else {
//                builder.append(c.getName());
//            }
//        }
//        return builder.toString();
//    }
//    static String toString(Class<?> c) {
//        if (c.isArray()) {
//            return c.getComponentType().getName() + "[]";
//        }
//        return c.getName();
//    }
//}

//static String methods(Object o) throws Throwable {
//    if (o == null) return "null";
//    final class Indent {
//        int count = 0;
//        public String toString() {
//            if (count == 0) {
//                return "";
//            }
//            char[] chars = new char[4 * count];
//            Arrays.fill(chars, ' ');
//            return new String(chars);
//        }
//    }
//    Indent indent = new Indent();
//    Class<?> c = (o instanceof Class) ? (Class<?>) o : o.getClass();
//    Class<?>[] interfaces = c.getInterfaces();
//    final StringBuilder builder = new StringBuilder()
//            .append(Modifier.toString(c.getModifiers()))
//            .append(' ')
//            .append(c.getName());
//    if (interfaces != null
//            && interfaces.length > 0) {
//        boolean isInterfacesFirst = true;
//        for (Class<?> i: interfaces) {
//            if (!isInterfacesFirst) {
//                builder.append(", ");
//            } else {
//                builder.append(" implements ");
//                isInterfacesFirst = false;
//            }
//            builder.append(i.getName());
//        }
//    }
//    builder.append(" {\n ");
//    indent.count = 1;
//    boolean isFirst = true;
//    while (true) {
//        Method[] dm = c.getDeclaredMethods();
//        if (!isFirst) {
//            indent.count = indent.count + 1;
//            builder.append("\n \n")
//                    .append(indent)
//                    .append("from ")
//                    .append(c.getName())
//                    .append(": {");
//            indent.count = indent.count + 1;
//        }
//        for (Method m: dm) {
//            m.setAccessible(true);
//            builder.append('\n')
//                    .append(indent)
//                    .append(Modifier.toString(m.getModifiers()))
//                    .append(' ')
//                    .append(ClassString.toString(m.getReturnType()))
//                    .append(' ')
//                    .append(m.getDeclaringClass().getName())
//                    .append('.')
//                    .append(m.getName())
//                    .append('(')
//                    .append(ClassString.toString(m.getParameterTypes()))
//                    .append(')');
//            Class<?>[] ets = m.getExceptionTypes();
//            boolean isETSFirst = true;
//            if (ets != null
//                    && ets.length > 0) {
//                builder.append(" throws ");
//                for (Class<?> et: ets) {
//                    if (!isETSFirst) {
//                        builder.append(", ");
//                    } else {
//                        isETSFirst = false;
//                    }
//                    builder.append(et.getName());
//                }
//            }
//        }
//
//        indent.count = indent.count - 1;
//        if (isFirst) {
//            isFirst = false;
//        } else {
//            builder.append('\n')
//                .append(indent)
//                .append("}");
//            indent.count = indent.count - 1;
//        }
//
//        c = c.getSuperclass();
//
//        if (c == null) {
//            break;
//        }
//    }
//
//    builder.append("\n};");
//
//    return builder.toString();
//}

//static String dict(Object o) throws Throwable {
//    if (o == null) return "null";
//    final class Indent {
//        int count = 0;
//        public String toString() {
//            if (count == 0) {
//                return "";
//            }
//            char[] chars = new char[4 * count];
//            Arrays.fill(chars, ' ');
//            return new String(chars);
//        }
//    }
//    Indent indent = new Indent();
//    Class<?> c = o.getClass();
//    StringBuilder builder = new StringBuilder()
//            .append(Modifier.toString(c.getModifiers()))
//            .append(' ')
//            .append(ClassString.toString(c))
//            .append(" {\n ");
//    indent.count++;
//    boolean isFirst = true;
//    while (true) {
//        if (!isFirst) {
//            builder.append("\n \n")
//                    .append(indent)
//                    .append("from class ")
//                    .append(c.getName())
//                    .append(" {");
//            indent.count++;
//        }
//        Field[] fields = c.getDeclaredFields();
//        if (fields != null
//                && fields.length > 0) {
//            for (Field field: fields) {
//                field.setAccessible(true);
//                builder.append(" \n")
//                        .append(indent)
//                        .append(Modifier.toString(field.getModifiers()))
//                        .append(' ')
//                        .append(ClassString.toString(field.getType()))
//                        .append(' ')
//                        .append(field.getName())
//                        .append(" = ");
//                Object obj = field.get(o);
//                String v;
//                if (obj == null) {
//                    v = "null";
//                } else if (obj.getClass().isArray()) {
//                    Class<?> objC = obj.getClass();
//                    Class<?> cT = objC.getComponentType();
//                    if (cT.isPrimitive()) {
//                        if (cT.equals(Byte.TYPE)) {
//                            v = Arrays.toString((byte[]) obj);
//                        } else if (cT.equals(Boolean.TYPE)) {
//                            v = Arrays.toString((boolean[]) obj);
//                        } else if (cT.equals(Character.TYPE)) {
//                            v = Arrays.toString((char[]) obj);
//                        } else if (cT.equals(Short.TYPE)) {
//                            v = Arrays.toString((short[]) obj);
//                        } else if (cT.equals(Integer.TYPE)) {
//                            v = Arrays.toString((int[]) obj);
//                        } else if (cT.equals(Long.TYPE)) {
//                            v = Arrays.toString((long[]) obj);
//                        } else if (cT.equals(Float.TYPE)) {
//                            v = Arrays.toString((float[]) obj);
//                        } else if (cT.equals(Double.TYPE)) {
//                            v = Arrays.toString((double[]) obj);
//                        } else {
//                            v = "void[]";
//                        }
//                    } else {
//                        v = Arrays.toString((Object[]) obj);
//                    }
//                } else {
//                    v = String.valueOf(obj);
//                }
//                builder.append(v);
//            }
//        }
//
//        if (!isFirst) {
//            indent.count--;
//            builder.append('\n')
//                    .append(indent)
//                    .append("}\n ");
//        } else {
//            isFirst = false;
//        }
//
//        c = c.getSuperclass();
//        if (c == null) {
//            break;
//        }
//    }
//    indent.count = 0;
//    builder.append(indent)
//            .append("};");
//    return builder.toString();
//}

//static Object get(Object o, String name) throws Throwable {
//    if (o == null) return null;
//    Class<?> c = o.getClass();
//    Field[] fields = c.getDeclaredFields();
//    if (fields == null
//            || fields.length == 0) {
//        return null;
//    }
//    for (Field f: fields) {
//        if (f.getName().equals(name)) {
//            f.setAccessible(true);
//            return f.get(o);
//        }
//    }
//    return null;
//}

//static void set(Object o, String name, Object w) throws Throwable {
//    if (o == null) return;
//    Class<?> c = o.getClass();
//    Field[] fields = c.getDeclaredFields();
//    if (fields == null
//            || fields.length == 0) {
//        return;
//    }
//    for (Field f: fields) {
//        if (f.getName().equals(name)) {
//            f.setAccessible(true);
//            int m = f.getModifiers();
//            if (Modifier.isFinal(m)) {
//                Field modField = Field.class.getDeclaredField("modifiers");
//                modField.setAccessible(true);
//                modField.setInt(f, m & ~Modifier.FINAL);
//            }
//            f.set(o, w);
//            return;
//        }
//    }
//}
}
