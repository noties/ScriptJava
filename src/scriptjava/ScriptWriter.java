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

import scriptjava.buildins.*;
import scriptjava.parser.*;

import java.io.File;
import java.util.*;

class ScriptWriter implements SystemInputReader.OnNewLineListener {

    private static final String SCRIPT_SOURCE_NAME = "Script.java";
    private static final String SCRIPT_CLASS_NAME = "Script";

    private static final String[] DEFAULT_IMPORTS = {
            IScript.class.getName(),
            "java.util.*",
            "java.text.*",
            "java.io.*"
    };

    static final Class<?>[] BUILD_INS = {
            Buildins.class,
            Length.class,
            Bool.class,
            Reflect.class,
            Network.class,
            Storage.class,
            Exec.class,
            Json.class,
            Math.class,
            IO.class,
            Range.class,
            Str.class,
            Copy.class
    };

    static final String[] STATIC_IMPORTS;
    static {
        final int length = Length.len(BUILD_INS);
        STATIC_IMPORTS = new String[length];
        for (int i = 0; i < length; i++) {
            STATIC_IMPORTS[i] = BUILD_INS[i].getName();
        }
    }

    interface OnTerminationListener {
        void terminate();
    }

    private final File scriptFolder;
    private final ScriptCompiler compiler;
    private final ScriptClassLoader classLoader;
    private final OnTerminationListener onTerminationListener;

    private final JavaLineParser javaLineParser;
    private final List<LineParser> parsers;

    private boolean isTerminated;
    private boolean isContinuousStatement;
    private int indent;

    ScriptWriter(File executionFolder, File scriptFolder, OnTerminationListener listener) {
        this.scriptFolder = scriptFolder;
        this.compiler = new ScriptCompiler(executionFolder, scriptFolder);
        this.classLoader = new ScriptClassLoader(scriptFolder);
        this.onTerminationListener = listener;
        this.javaLineParser = new JavaLineParser();
        this.parsers = Arrays.asList(new SpecialCommandLineParser(), javaLineParser);
    }

    // called before we start to listen to commands
    void prepare() {

        isContinuousStatement = false;

        lineStart();
    }

    void lineStart() {
        final String start;
        if (indent == 0) {
            if (isContinuousStatement) {
                start = "... ";
            } else {
                start = ">>> ";
            }
        } else {
            if (!isContinuousStatement) {
                throw new RuntimeException("Unexpected state");
            }
            final StringBuilder builder = new StringBuilder();
            for (int i = 0; i < indent + 1; i++) {
                builder.append("...");
            }
            builder.append(' ');
            start = builder.toString();
        }
        System.out.print(start);
    }

    @Override
    public void onNewLine(String line) {

        // no need to handle new input
        if (isTerminated) {
            return;
        }

        // also, I think it's cool to pre-parse our helper commands (set, get, etc)

        // ok, next we need to determine what we have here:
        // - special command
        // - method variable (that is used inside out `execute` method)
        // - everything else, that cannot be inside a method (instance method, class... field?)

        for (LineParser parser: parsers) {
            if (parser.parse(line)) {
                break;
            }
        }

        // line starting at the end of this cycle
        lineStart();
    }

    void terminate() {
        isTerminated = true;
        onTerminationListener.terminate();
    }

    // returns TRUE if compilation was successful
    boolean compileAndRun(String source) {
        if (compiler.compile(SCRIPT_SOURCE_NAME, source)) {
            try {
                final IScript script = classLoader.createInstance(SCRIPT_CLASS_NAME);
                script.execute();
            } catch (Throwable t) {
                t.printStackTrace();
                return false;
            }
            return true;
        }
        return false;
    }

    // not static
    private class SpecialCommandLineParser implements LineParser {

        private static final String COMMAND_QUIT    = "quit";
        private static final String COMMAND_IMPORT = "import ";
        private static final String COMMAND_CLEAN = "clean";
//        private static final String COMMAND_SAVE = "save ";
//        private static final String COMMAND_EXEC = "exec ";
        private static final String COMMAND_INCLUDE = "include ";
        private static final String COMMAND_BYTECODE = "bytecode";

        // todo, dynamic include, script file format for (save, exec) *.sj

        @Override
        public boolean parse(String line) {

            if (COMMAND_QUIT.equals(line)) {
                terminate();
                return true;
            }

            if (line.startsWith(COMMAND_IMPORT)) {
                javaLineParser.addImport(line);
                return true;
            }

            if (line.equals(COMMAND_CLEAN)) {
                javaLineParser.clean();
                return true;
            }
//
//            if (line.startsWith(COMMAND_SAVE)) {
//                return true;
//            }
//
//            if (line.startsWith(COMMAND_EXEC)) {
//                // we just compile what we have here
//                // todo.
//            }

            if (line.equals(COMMAND_BYTECODE)) {
                // create path to our compiled classes
                final String path = scriptFolder.getAbsolutePath() + File.separator + "Script*.class";
                Buildins.print(Exec.exec("javap -p -c " + path));
                return true;
            }

            return false;
        }
    }

    @SuppressWarnings({"unchecked", "finally", "deprecation", "path", "serial", "fallthrough"})
    private class JavaLineParser implements LineParser {

        // no package, no imports, definition, execute method, everything else
        private static final String CLASS_BODY = "%1$s\n\n" +
                "@SuppressWarnings({\"unchecked\", \"finally\", \"deprecation\", \"path\", \"serial\", \"fallthrough\"})\n" +
                "public class Script implements scriptjava.IScript {\n\n" +
                "public void execute() throws Throwable {\n" +
                "%2$s\n" +
                "}\n\n" +
                "%3$s\n" +
                "}";

        // ok, of what do we care
        // - new class declaration
        // - new method declaration (can be merged with class)
        // - constant declaration (can be merged with of top)
        // - inner fields declaration (also)
        // - constructor is disabled, but we may include static & {} declarations
        // - if/while/for -> execution declaration
        // - abstract instance -> execution declaration, h, `isPrintStatement`
        // - lambda??
        //

        // todo, save variables between executes? so they are the same and not the new ones
        // each time the script compiled and run

        private final StatementParser parser;
        private final Set<String> imports;
        private final StringBuilder executeBody;
        private final StringBuilder membersBody;

        private int started;
        private StringBuilder pendingBody;

        private boolean isPrintStatement;
        private boolean isVariable;
        private StatementType previousType;

        JavaLineParser() {
            this.parser = new StatementParserManual(BUILD_INS);
            this.imports = new HashSet<>();
            this.executeBody = new StringBuilder();
            this.membersBody = new StringBuilder();
        }

        @Override
        public boolean parse(String line) {

            // here we need to do all the parsing...
            // like if it's a statement, that should be saved (only for printing)

            // we can detect if statement is ongoing if we count the opening/closing brackets
            // we don't need to track the actual order -> compile wont' compile such a thing

            // ok, if we still have pendingBody -> just append

            // check for possible substitutions
            line = StatementSubstitution.substitute(line);


            if (pendingBody != null && (isContinuousStatement || StatementType.EXECUTION != previousType)) {
                pendingBody.append(line)
                        .append('\n');
            } else {
                final Statement statement = parser.parse(line);
                previousType = statement.type();
                switch (previousType) {

                    case MEMBER:
                        pendingBody = membersBody;
                        break;

                    case EXECUTION:
                        pendingBody = executeBody;
                        break;

                    default:
                        throw new RuntimeException("Unknown statement type: " + statement.type());
                }

                isPrintStatement = statement.isPrintStatement();
                isVariable = statement.isVariable();

                if (!isContinuousStatement) {
                    started = pendingBody.length();
                }

                final String code;
                if (isPrintStatement) {
                    code = "print(" + statement.code() + ");";
                } else {
                    code = statement.code();
                }

                pendingBody.append(code);

//                if (isContinuousStatement) {
//                    pendingBody.append('\n');
//                }

//                pendingBody.append('\n');
            }

            // ok, check inputState for the line -> if complete -> check if ends with ';' -> if not add it
            // then check inputState for all the pending builder
            final InputState statementState = isPrintStatement ? null : inputState(line);
            if (statementState != null && statementState.indent == 0) {
                // check if we need to attach the `;`
                if (line.charAt(line.length() - 1) != ';') {
                    pendingBody.append(';');
                }
            }

            if (!isContinuousStatement) {
                pendingBody.append('\n');
            }

            final InputState bodyState = inputState(pendingBody);
            isContinuousStatement = bodyState.isContinuousStatement;
            indent = bodyState.indent;

//            // to find a way to keep the indent the right way...
//            {
//                final int previousIndent = indent;
//                indent = bodyState.indent;
//                if (indent < previousIndent) {
//                    // let's try to erase the whole line...
//                    final StringBuilder builder = new StringBuilder(20);
//                    for (int i = 0; i < 20; i++) {
//                        builder.append('\b');
//                    }
//                    System.out.print(builder);
//                    lineStart();
//                    System.out.print(line);
//                }
//            }

            if (!isContinuousStatement) {

                if (!compileAndRun(classSource())) {
                    pendingBody.setLength(started);
                } else {
                    // and not variable...
                    // everything except variables will be removed from execution block
                    if (StatementType.EXECUTION == previousType && !isVariable) {
                        pendingBody.setLength(started);
                    }
                }

                pendingBody = null;
            }

            return true;
        }

        void addImport(String what) {
            if (what.startsWith("import ")) {
                // we need to remove it
                what = what.substring("import ".length());
            }
            if (what.charAt(what.length() - 1) == ';') {
                what = what.substring(0, what.length() - 1);
            }
            imports.add(what);
        }

        void clean() {
            imports.clear();
            membersBody.setLength(0);
            executeBody.setLength(0);
            isContinuousStatement = false;
            isPrintStatement = false;
            started = 0;
            previousType = null;
        }

        private String classSource() {
            final String importsBody;
            // imports
            {
                final StringBuilder builder = new StringBuilder();

                // def imports must always be here
                for (String s: DEFAULT_IMPORTS) {
                    builder.append("import ")
                            .append(s)
                            .append(';')
                            .append('\n');
                }

                for (String s: STATIC_IMPORTS) {
                    builder.append("import static ")
                            .append(s)
                            .append(".*;\n");
                }

                for (String s: imports) {
                    builder.append("import ")
                            .append(s)
                            .append(';')
                            .append('\n');
                }

                importsBody = builder.toString();
            }

            return String.format(Locale.US, CLASS_BODY, importsBody, executeBody.toString(), membersBody.toString());
        }
    }

    private static InputState inputState(CharSequence cs) {
        final boolean result;
        final int indent;
        if (cs == null || cs.length() == 0) {
            result = true;
            indent = 0;
        } else {
            int open = 0;
            char c;
            for (int i = 0, length = cs.length(); i < length; i++) {
                c = cs.charAt(i);
                if ('{' == c) {
                    open += 1;
                } else if ('}' == c) {
                    open -= 1;
                }
            }
            result = open == 0;
            indent = open;
        }
        return new InputState(!result, indent);
    }

    private static class InputState {

        final boolean isContinuousStatement;
        final int indent;

        private InputState(boolean isContinuousStatement, int indent) {
            this.isContinuousStatement = isContinuousStatement;
            this.indent = indent;
        }
    }
}
