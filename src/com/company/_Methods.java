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
            "static class Indent {\n" +
                    "    int count = 0;\n" +
                    "    public String toString() {\n" +
                    "        if (count == 0) {\n" +
                    "            return \"\";\n" +
                    "        }\n" +
                    "        char[] chars = new char[4 * count];\n" +
                    "        Arrays.fill(chars, ' ');\n" +
                    "        return new String(chars);\n" +
                    "    }\n" +
                    "}\n" +
                    "\n" +
                    "static String methods(Object o) throws Throwable {\n" +
                    "    if (o == null) return \"null\";\n" +
                    "    Indent indent = new Indent();\n" +
                    "    Class<?> c = o.getClass();\n" +
                    "    final StringBuilder builder = new StringBuilder()\n" +
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
                    "                    .append(Arrays.toString(m.getParameterTypes()))\n" +
                    "                    .append(')');\n" +
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

//static class Indent {
//    int count = 0;
//    public String toString() {
//        if (count == 0) {
//            return "";
//        }
//        char[] chars = new char[4 * count];
//        Arrays.fill(chars, ' ');
//        return new String(chars);
//    }
//}
//
//static String methods(Object o) throws Throwable {
//    if (o == null) return "null";
//    Indent indent = new Indent();
//    Class<?> c = o.getClass();
//    final StringBuilder builder = new StringBuilder()
//            .append(c.getName())
//            .append(": {\n ");
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
//                    .append(m.getReturnType().getName())
//                    .append(' ')
//                    .append(m.getDeclaringClass().getName())
//                    .append('.')
//                    .append(m.getName())
//                    .append('(')
//                    .append(Arrays.toString(m.getParameterTypes()))
//                    .append(')');
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
}
