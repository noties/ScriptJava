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

package scriptjava.parser;

import scriptjava.buildins.Length;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementParserRegExp extends StatementParserBase {

    private static final Pattern DETECT_METHOD = Pattern.compile("^((static|final)\\s)*(\\w+\\s*)+\\(.*\\)\\s*\\{\\s*");
    private static final Pattern DETECT_CLASS = Pattern.compile("^(static\\s)*(class)\\s+(\\w+\\s(.)*)\\{\\s*");
    private static final Pattern DETECT_VARIABLE = Pattern.compile("^((static|final)\\s+)*\\w+((\\[\\])*(<.*>)*)*\\s+\\w+(\\[\\])*\\s*(=\\s*(\\w|\")+\\s*(.[^\\{])*)*;*$");
    private static final Pattern DETECT_VOID_CALL = Pattern.compile("^(\\w+\\.)*\\w+\\s*\\(.*");
    private static final Pattern DETECT_BUILD_IN = Pattern.compile("^(\\w+)\\s*\\(.*\\);*");
    private static final Pattern DETECT_VAR_NEW_VALUE = Pattern.compile("^\\w+\\s*=\\s*\\w+.*");

    public StatementParserRegExp(Class<?>[] buildIns) {
        super(buildIns);
    }

    // ok, of what do we care
    // - new class declaration
    // - new method declaration (can be merged with class)
    // - constant declaration (can be merged with of top)
    // - inner fields declaration (also)
    // - constructor is disabled, but we may include static {} & {} declarations
    // - if/while/for -> execution declaration
    // - abstract instance -> execution declaration, h, `isPrintStatement`
    // - lambda??
    //

    @Override
    public Statement parse(String line) {

        final Statement statement;

        // if first char is '{' -> assume it's instance declaration
        // the same for `static {`
        if ('{' == line.charAt(0)
                || line.startsWith("static {")) {
            statement = new StatementMember(line);
        } else {

            // let's remove all visibility modifiers for easier parsing
            final String clean = removeVisibilityModifiers(line);

//            System.out.printf("clean: %s\n", clean);
//            System.out.printf("clean: %s, isMethod: %s, isVariable: %s, isVoidCall: %s\n", clean, isMethodOrClassDeclaration(clean), isVariableDeclaration(clean), isVoidCall(line));

            if (isMethodOrClassDeclaration(clean)) {
                // let's check if it's a if/while/for/do
                // todo, improve... optional space & required `(`
                if (line.startsWith("if")
                        || line.startsWith("while")
                        || line.startsWith("do")
                        || line.startsWith("for")) {
                    statement = new StatementExecution(line, false);
                } else {
                    statement = new StatementMember(line);
                }
            } else if (isVariableDeclaration(clean)) {
                // here we can see -> member variable(const,field) or execution variable
                // if we have visibility modifiers -> member, else execution
                if (line.startsWith("private")
                        || line.startsWith("public")
                        || line.startsWith("protected")
                        || line.startsWith("static")) {
                    statement = new StatementMember(line);
                } else {
                    // todo, in case of new variable creation we can immediately print it
                    statement = new StatementExecution(line, false);
                }
            } else if (isVoidCall(line)) { // detect void call +! variable reintialization aka `i = 0`
                // here it could be our build in void call -> check it first
                if (isBuildInStatement(line)) {
                    if (line.startsWith("print")) {
                        // it's our buildIn statement print
                        // no need to wrap it around `print()`
                        // so, just return what we have
                        statement = new StatementExecution(line + (line.charAt(line.length() - 1) != ';' ? ";" : ""), true);
                    } else {
                        statement = new StatementExecution(printCode(line), true);
                    }
                } else {
                    statement = new StatementExecution(line, false);
                }
            } else if(isNewValueForVariable(line)) {
                statement = new StatementExecution(line, false);
            } else {
                // otherwise just print
                statement = new StatementExecution(printCode(line), true);
            }
        }

        // if starts with `static {` -> class declaration

        // remove MODIFIERS for easier parsing? TRUE!!!

        // PLEASE NOTE NO `package-private` for inner members -> must be execution variable
        // (private|public|protected)(static)(final) $type $name ($NAME) (= $value)
        // for constants must be initialized (won't compile otherwise)
        // for private fields initialization can be skipped

        // maybe we can change this thing a bit... for example `const String s = "34"`

        return statement;
    }

    private boolean isBuildInStatement(String line) {
        final Matcher matcher = DETECT_BUILD_IN.matcher(line);
        return matcher.matches() && buildIns.contains(matcher.group(1));
    }

    private static boolean isMethodOrClassDeclaration(String in) {
        // (static) class $name {
        // $return_type $name ($arguments) {
        return DETECT_METHOD.matcher(in).matches()
                || DETECT_CLASS.matcher(in).matches();
    }

    private static boolean isVariableDeclaration(String in) {
        return DETECT_VARIABLE.matcher(in).matches();
    }

    private static boolean isVoidCall(String in) {
        return DETECT_VOID_CALL.matcher(in).matches();
    }

    private static boolean isNewValueForVariable(String in) {
        return DETECT_VAR_NEW_VALUE.matcher(in).matches();
    }

    private static String removeVisibilityModifiers(String in) {

        // absolutely has no modifiers
        if (Length.len(in) < 6) {
            return in;
        }

        return in.replaceAll("(public|private|protected)", "").trim();
    }

    private static String printCode(String line) {
        return String.format("print(%s);", line);
    }
}
