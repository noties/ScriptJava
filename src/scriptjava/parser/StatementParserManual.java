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

package scriptjava.parser;

import scriptjava.buildins.Bool;

import java.lang.reflect.Modifier;
import java.util.Set;

public class StatementParserManual extends StatementParserBase {

    public StatementParserManual(Class<?>[] buildIns) {
        super(buildIns);
    }

    @Override
    public Statement parse(String line) {
        return new LineParser(buildIns, line).parse();
    }

    private static class LineParser {

        private final Set<String> buildIns;
        private final String line;
        private final int length;

        int index = -1;
        char c;

        LineParser(Set<String> buildIns, String line) {
            this.buildIns = buildIns;
            this.line = line;
            this.length = line.length();
        }

        Statement parse() {

            next();

            // the `static {` & `{`
            if (isInitializationBlock()) {
                return new StatementMember(line);
            }

            // let's check modifiers
            final int modifiers = modifiers();

            // ok, if modifiers are already interface|abstract|(visibility)|static|synchronized|native|transient|volatile
            // then it's a member
            if (isMemberModifiers(modifiers)) {
                return new StatementMember(line);
            }

            // ok, as we have passed modifiers at this point check what is it next: class, method (without modifiers)
            if (isClassDeclaration()) {
                return new StatementMember(line);
            }

            // loops & if/else
            if (isCodeBlock()) {
                return new StatementExecution(line, false);
            }

            // check if it's void call
            // check if it's out build-in method call
            final Statement methodOrVariable = methodOrVariable();
            if (methodOrVariable != null) {
                return methodOrVariable;
            }

            // just in case
            return new StatementExecution(line, true);
        }

        private boolean isMemberModifiers(int modifiers) {
            return Modifier.isPublic(modifiers)
                    || Modifier.isPrivate(modifiers)
                    || Modifier.isProtected(modifiers)
                    || Modifier.isStatic(modifiers)
                    || Modifier.isInterface(modifiers)
                    || Modifier.isAbstract(modifiers)
                    || Modifier.isSynchronized(modifiers)
                    || Modifier.isNative(modifiers)
                    || Modifier.isTransient(modifiers)
                    || Modifier.isVolatile(modifiers);
        }

//        static void log(String message, Object... args) {
//            System.out.printf(message, args);
//            System.out.println();
//        }

        boolean next() {
            return next(1);
        }

        boolean next(int step) {
            final boolean result;
            index += step;
            if (!canRead()) {
                c = '\0';
                result = false;
            } else {
                c = line.charAt(index);
                result = true;
            }
            return result;
        }

        private boolean isInitializationBlock() {

            if (!clearSpaces()) {
                return false;
            }

            final int started = index;

            boolean result = false;

            switch (c) {

                case '{':
                    result = true;
                    break;

                case 's':
                    result = read("tatic {") || read("tatic{");
                    if (!result) {
                        reset(started);
                    }
                    break;
            }

            return result;
        }

        private int modifiers() {

            // in case we didn't match any of the modifiers, we must set the `i` to initial value
            int started = index;

            int mod = 0;

            boolean reading = true;

            while (reading) {

                if (isWhiteSpace()) {
                    if (!next()) {
                        reading = false;
                    } else {
                        continue;
                    }
                }

                if (reading) {

                    switch (c) {

                        case 'p':

                            if (!next()) {
                                reading = false;
                                break;
                            }

                            // read public private protected

                            if ('u' == c) {
                                // public
                                if (read("blic ")) {
                                    mod += Modifier.PUBLIC;
                                } else {
                                    // we didn't match the public
                                    reading = false;
                                }
                            } else if ('r' == c) {

                                if (!next()) {
                                    reading = false;
                                    break;
                                }

                                // private | protected

                                if ('i' == c) {
                                    // private
                                    if (read("vate ")) {
                                        mod += Modifier.PRIVATE;
                                    } else {
                                        reading = false;
                                    }
                                } else if ('o' == c) {
                                    // protected
                                    if (read("tected ")) {
                                        mod += Modifier.PROTECTED;
                                    } else {
                                        reading = false;
                                    }
                                } else {
                                    reading = false;
                                }
                            }
                            break;

                        case 's':

                            if (!next()) {
                                reading = false;
                                break;
                            }

                            // read static, synchronized
                            // 's' -> strictfp is not supported
                            if ('t' == c) {
                                // static
                                if (read("atic ")) {
                                    mod += Modifier.STATIC;
                                } else {
                                    reading = false;
                                }
                            } else if ('y' == c) {
                                // synchronized
                                if (read("nchronized ")) {
                                    mod += Modifier.SYNCHRONIZED;
                                } else {
                                    reading = false;
                                }
                            } else {
                                reading = false;
                            }
                            break;

                        case 'f':
                            // read final
                            if (read("inal ")) {
                                mod += Modifier.FINAL;
                            } else {
                                reading = false;
                            }
                            break;

                        case 'a':
                            // read abstract
                            if (read("bstract ")) {
                                mod += Modifier.ABSTRACT;
                            } else {
                                reading = false;
                            }
                            break;

                        case 't':
                            // read transient
                            if (read("ransient")) {
                                mod += Modifier.TRANSIENT;
                            } else {
                                reading = false;
                            }
                            break;

                        case 'v':
                            // read volatile
                            if (read("olatile ")) {
                                mod += Modifier.VOLATILE;
                            } else {
                                reading = false;
                            }
                            break;

                        case 'n':
                            // read native
                            if (read("ative ")) {
                                mod += Modifier.NATIVE;
                            } else {
                                reading = false;
                            }
                            break;

                        case 'i':
                            // read interface
                            if (read("nterface ")) {
                                mod += Modifier.INTERFACE;
                            } else {
                                reading = false;
                            }
                            break;

                        default:
                            reading = false;
                    }
                }
            }

            // we didn't match anything -> revert to the started position
            if (mod == 0) {
                reset(started);
            }

            return mod;
        }

        private boolean isClassDeclaration() {

            // class Name
            // <Type> name () {

            final int started = index;

            boolean result = false;

            if (!clearSpaces()) {
                return false;
            }

            if ('c' == c) {
                // check if it's `class `
                // else if could be return type for a method
                if (read("lass ")) {
                    result = true;
                }
            }

            if (!result) {
                reset(started);
            }

            return result;
        }

        private Statement methodOrVariable() {

            // Method declaration: <Type> <Space> <Name> <*Space> <(> <*> <)>
            // Method call: <*Type && `.`><Name><*Space><(><*><)>

            // Variable declaration: <Type> <Space> <Name> <*Space> <`=`> <*>
            // Variable reassignment: <Name> <*Space> <`=`> <*>

            // SomeClass.<String>myMethod(null);
            // new Thread(/**/).start();
            // new Date() { public void toString() { return "date"; } }; -> `new` should not be type
            // SomeClass.SomeInnerClass nameOfTheMethod();
            // chain1().chain2().chain3();
            // chain1().field = null;
            // (true == true) -> is not a method call
            // also... `String s`; -> must be valid variable declaration...

            // ok, it looks like we must start from the end of the line

            if (!clearSpaces()) {
                return null;
            }

            final int started = index;

            char ch;

            boolean isMethod = false;
            boolean isVariable = false;

            // method: wee need to find the most `()` to the left

            int i;

            for (i = index; i < length; i++) {
                ch = line.charAt(i);
                if ('(' == ch) {
                    isMethod = true;
                    break;
                } else if('!' == ch) {
                    break;
                } else if ('=' == ch) {
                    // but we need to be sure that it's not `==`
                    i += 1;
                    if (line.charAt(i) == '=') {
                        // it's not a variable it's a boolean expression
                        break;
                    } else {
                        isVariable = true;
                        break;
                    }
                }
            }

            i -= 1;

            final Statement statement;

            if (isMethod) {

                Statement method = null;

                // let's check if there is a caller info previously to `(.*)`
                // and parse it -> if it contains return type -> method declaration
                // else method call
                // if it's method declaration -> the name cannot contain anything except valid java identifier (no generics, no dots)

                String name = null;
                char methodChar;

                final StringBuilder builder = new StringBuilder();

                outer:
                for (int m = i; m >= index; m--) {

                    // the space can be: before `(` -> optional
                    // the space can be: before <Name>

                    methodChar = line.charAt(m);

                    if (!Character.isWhitespace(methodChar)) {

                        // here we start, depending on the state:
                        // parsing name
                        // parsing type (we can skip parsing the full type, as long as there is a least one char -> it's it)

                        if (Bool.bool(name)) {

                            // we already have name -> it's type info
                            // also need to insert a check if type equals `new` -> simple method call, not method declaration

                            builder.setLength(0);

                            char innerChar;
                            for (int m2 = m; m2 >= index; m2--) {

                                innerChar = line.charAt(m2);

                                if (Character.isWhitespace(innerChar)) {
                                    // if we have out builder here -> stop
                                    // else skip
                                    if (builder.length() == 0) {
                                        continue;
                                    }
                                }

                                // to ignore all possible operations: `+`, etc
                                if (Character.isLetterOrDigit(innerChar)) {
                                    builder.insert(0, innerChar);
                                } else {

                                    // but we must ignore all the `(),`

                                    // check for `=` sign?
                                    if ('=' == innerChar) {
                                        // check fo bool expressions...
                                        m2 -= 1;
                                        if ('=' == line.charAt(m2) || '!' == line.charAt(m2)) {
                                            // boolean
                                        } else {
                                            // variable?
                                            method = new StatementExecution(line, false);
                                        }
                                    }
                                    name = null;
                                    builder.setLength(0);
                                    break outer;
                                }
                            }

                            // if it's just simple `new Date()` for example -> just execute it
                            if ("new".equals(builder.toString())) {
                                method = new StatementExecution(line, false);
                            } else {
                                method = new StatementMember(line);
                            }
                            break;
                        } else if ('.' == methodChar) {
                            // CHECK here if it's a dot `.` -> method call
                            name = builder.toString();
                            builder.setLength(0);
                            break;
                        } else {

                            // if name is still not present
                            // in case of wrapped calls `bool(file())`
                            if (Character.isLetterOrDigit(methodChar)) {
                                builder.insert(0, methodChar);
                            } else {
                                name = null;
                                builder.setLength(0);
                            }
                        }
                    } else {

                        // todo, else let's check if we have something previously
                        // if we have -> we need to parse it also
                        if (!Bool.bool(name) && builder.length() != 0) {
                            name = builder.toString();
                            builder.setLength(0);
                        }
                    }
                }

                if (method != null) {
                    statement = method;
                } else {

                    // method call?
                    // if name != null || builder.length > 0
                    if (!Bool.bool(name)) {
                        name = builder.toString();
                    }

                    if (Bool.bool(name)) {
                        // check if it's our build-in method
                        final boolean isPrint = !"print".equals(name) && !"printf".equals(name) && buildIns.contains(name);
                        statement = new StatementExecution(line, isPrint);
                    } else {
                        statement = null;
                    }
                }

            } else if (isVariable) {

                // two possible states -> new variable (local to execution state) & reassignment
                // if it's simply <Name><`=`> -> reassignment, else declaration

                // Variable declaration: <Type> <Space> <Name> <*Space> <`=`> <*>
                // Variable reassignment: <Name> <*Space> <`=`> <*>

                statement = new StatementExecution(line, false, true);

            } else {
                statement = null;
            }

            if (statement == null) {
                reset(started);
            }

            return statement;
        }

        // loops & if/else
        private boolean isCodeBlock() {

            // if
            // for
            // while
            // do

            if (!clearSpaces()) {
                return false;
            }

            final int started = index;

            boolean result = false;

            switch (c) {

                case 'i':
                    // if
                    result = read("f ") || read("f(");
                    break;

                case 'f':
                    // for
                    result = read("or ") || read("or(");
                    break;

                case 'w':
                    // while:
                    result = read("hile ") || read("hile(");
                    break;

                case 'd':
                    // do
                    result = read("o ") || read("o{");
                    break;
            }

            if (!result) {
                reset(started);
            }


            return result;
        }

        private boolean isWhiteSpace() {
            return Character.isWhitespace(c);
        }

        private boolean canRead() {
            return index < length;
        }

        private boolean canRead(int plus) {
            return (index + plus) < length;
        }

        private boolean clearSpaces() {
            while (isWhiteSpace()) {
                if (!next()) {
                    return false;
                }
            }
            return true;
        }

        private void reset(int position) {
            index = position;
            c = line.charAt(index);
        }

        private boolean read(String what) {

            final int readLength = what.length();

            // ok, here we must check that we are in bounds
            if (!canRead(readLength)) {
                return false;
            }

            boolean matched = true;

            for (int i = 0; i < readLength; i++) {
                if (next() && c == what.charAt(i)) {
                    continue;
                } else {
                    matched = false;
                }
            }

            if (matched) {
                next();
            }

            return matched;
        }
    }
}
