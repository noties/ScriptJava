package com.company;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 10.07.2015.
 */
class _Methods {

    static final String PRINT = "static String print(Object o){return String.valueOf(o);}";
    static final String PRINT_F = "static String printf(String s, Object... a){return String.format(s, a);}";
    static final String TYPE_OF = "static String typeof(Object o){if (o == null) {return null;} return String.valueOf(o.getClass());}";

    static final String METHODS =
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
                    "    final class ArraysString {\n" +
                    "        String toString(Class<?>[] classes) {\n" +
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
                    "    }\n" +
                    "    ArraysString arraysString = new ArraysString();\n" +
                    "    Class<?> c = (o instanceof Class) ? (Class<?>) o : o.getClass();\n" +
                    "    final StringBuilder builder = new StringBuilder()\n" +
                    "            .append(Modifier.toString(c.getModifiers()))\n" +
                    "            .append(' ')\n" +
                    "            .append(c.getName())\n" +
                    "            .append(\": {\\n \");\n" +
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
                    "                    .append(m.getReturnType().getName())\n" +
                    "                    .append(' ')\n" +
                    "                    .append(m.getDeclaringClass().getName())\n" +
                    "                    .append('.')\n" +
                    "                    .append(m.getName())\n" +
                    "                    .append('(')\n" +
                    "                    .append(arraysString.toString(m.getParameterTypes()))\n" +
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

static String methods(Object o) throws Throwable {
    if (o == null) return "null";
    final class Indent {
        int count = 0;
        public String toString() {
            if (count == 0) {
                return "";
            }
            char[] chars = new char[4 * count];
            Arrays.fill(chars, ' ');
            return new String(chars);
        }
    }
    Indent indent = new Indent();
    final class ArraysString {
        String toString(Class<?>[] classes) {
            if (classes == null
                    || classes.length == 0) {
                return "";
            }
            final StringBuilder builder = new StringBuilder();
            boolean isFirst = true;
            for (Class<?> c: classes) {
                if (!isFirst) {
                    builder.append(", ");
                } else {
                    isFirst = false;
                }
                if (c.isArray()) {
                    builder.append(c.getComponentType().getName())
                            .append("[]");
                } else {
                    builder.append(c.getName());
                }
            }
            return builder.toString();
        }
    }
    ArraysString arraysString = new ArraysString();
    Class<?> c = (o instanceof Class) ? (Class<?>) o : o.getClass();
    final StringBuilder builder = new StringBuilder()
            .append(Modifier.toString(c.getModifiers()))
            .append(' ')
            .append(c.getName())
            .append(": {\n ");
    indent.count = 1;
    boolean isFirst = true;
    while (true) {
        Method[] dm = c.getDeclaredMethods();
        if (!isFirst) {
            indent.count = indent.count + 1;
            builder.append("\n \n")
                    .append(indent)
                    .append("from ")
                    .append(c.getName())
                    .append(": {");
            indent.count = indent.count + 1;
        }
        for (Method m: dm) {
            m.setAccessible(true);
            builder.append('\n')
                    .append(indent)
                    .append(Modifier.toString(m.getModifiers()))
                    .append(' ')
                    .append(m.getReturnType().getName())
                    .append(' ')
                    .append(m.getDeclaringClass().getName())
                    .append('.')
                    .append(m.getName())
                    .append('(')
                    .append(arraysString.toString(m.getParameterTypes()))
                    .append(')');
            Class<?>[] ets = m.getExceptionTypes();
            boolean isETSFirst = true;
            if (ets != null
                    && ets.length > 0) {
                builder.append(" throws ");
                for (Class<?> et: ets) {
                    if (!isETSFirst) {
                        builder.append(", ");
                    } else {
                        isETSFirst = false;
                    }
                    builder.append(et.getName());
                }
            }
        }

        indent.count = indent.count - 1;
        if (isFirst) {
            isFirst = false;
        } else {
            builder.append('\n')
                .append(indent)
                .append("}");
            indent.count = indent.count - 1;
        }

        c = c.getSuperclass();

        if (c == null) {
            break;
        }
    }

    builder.append("\n};");

    return builder.toString();
}

//static String fields(Object o) {
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
//}
}
