package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dimitry Ivanov (mail@dimitryivanov.ru) on 09.07.2015.
 */
public class ScriptWriter implements ConsoleReader.Callback {

    private static final String JAVA_FILE = ".cache/Script.java";
    private static final String CACHE_FOLDER = ".cache";
    private static final String IMPORT = "import ";
    private static final List<String> INITIAL_IMPORTS = Arrays.asList(
            "import java.util.*;",
            "import static java.lang.System.out;"
    );
    private static final String QUIT = "quit()";
    private static final char SEMI_COLON = ';';
    private static final String STATIC = "static ";
    private static final String VOID = "void ";

    // used patterns
    private static final Pattern CLEAR_PATTERN = Pattern.compile("clear\\((.*)\\)");

    private static final Pattern METHOD_PATTERN = Pattern.compile("[A-Za-z]{1}\\w*\\s+\\w+\\s*\\(.*\\)\\s*\\{.*\\}?");

    private static final Pattern BLOCK_START_PATTERN = Pattern.compile(".*\\{.*");
    private static final Pattern BLOCK_END_PATTERN = Pattern.compile(".*\\}\\s*;*");

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\w{1}.*\\=.*");
    private static final Pattern VARIABLE_NAME_PATTERN = Pattern.compile("(.*\\s+)(\\w+)(\\s*\\=\\s*.*)");

    private PrintWriter writer;
    private final List<String> variables = new ArrayList<String>();
    private final List<String> imports = new ArrayList<String>();
    private final List<String> methods = new ArrayList<String>();
    private boolean shouldExecute = true;

    private StringBuilder currentMethod = new StringBuilder();
    private boolean isWritingMethod = false;
    private boolean isMethodCompilePhase = false;

    private boolean isVariable = false;
    private boolean isErrorHandled = false;

    private final Indent indent = new Indent(4);
    private int innerMethodBlocks = 0;

    public ScriptWriter() {
        final File file = new File(CACHE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    public void onNewLine(String line) {

        if (line.equals(QUIT)) {
            System.exit(0);
            return;
        }

        final StringWrapper wrapper = new StringWrapper(line);
        boolean handled = false;

        // clear
        handled = checkClear(wrapper);

        // imports
        if (!handled) {
            handled = checkImports(wrapper);
        }

        // method
        isMethodCompilePhase = false;
        if (!handled) {
            final ExecutionState methodState = checkMethod(wrapper);

            if (methodState == ExecutionState.RETURN) {
                return;
            }

            handled = methodState.value;
        }

        // variables
        isVariable = false;
        if (!handled) {
            final ExecutionState variablesState = checkVariable(wrapper);

            if (variablesState == ExecutionState.RETURN) {
                return;
            }

            handled = variablesState.value;
        }

        shouldExecute = true;
        isErrorHandled = false;

        try {
            writer = new PrintWriter(JAVA_FILE, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (writer == null) {
            return;
        }

        write(INITIAL_IMPORTS);
        write(imports);

        writeSuppressStatements();
        writer.println("public class Script {");
        writer.println("public static void main(String[] args){");

        writer.println("try {");
        write(variables);
        final String out = wrapper.getString();
        if (!TextUtils.isEmpty(out)) {
            handled = true;
            writer.println("System.out.println(" + out + ");");
        }
        writer.println("} catch (Throwable _throwable) { _throwable.printStackTrace(); }");
        writer.println("}");

        writeHelperMethods();

        write(methods);

        writer.println("}");

        writer.flush();
        writer.close();

        try {
            final Runtime runtime = Runtime.getRuntime();

            // changes were made
            if (handled) {
                final Process compileProcess = runtime.exec("javac -nowarn -Xlint:none " + JAVA_FILE);
                ProcessRedirect.redirect(compileProcess.getInputStream(), null);
                ProcessRedirect.redirect(compileProcess.getErrorStream(), new ProcessRedirect.Callback() {
                    @Override
                    public void apply() {
                        if (shouldExecute) {
                            shouldExecute = false;
                        }

                        if (!isErrorHandled) {
                            onCompileError();
                            isErrorHandled = true;
                        }
                    }
                });
                compileProcess.waitFor();
            }

            if (shouldExecute) {
                final Process process = runtime.exec("java -cp .cache/ Script");
                ProcessRedirect.redirect(process);
                process.waitFor();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void writeSuppressStatements() {
        writer.println("@SuppressWarnings({\"unchecked\", \"finally\", \"deprecation\", \"path\", \"serial\", \"fallthrough\"})");
    }

    private void writeHelperMethods() {
        writer.println("static String print(Object o){return String.valueOf(o);}");
        writer.println("static String printf(String s, Object... a){return String.format(s, a);}");
        writer.println("static String typeof(Object o){if (o == null) {return null;} return String.valueOf(o.getClass());}");
    }

    private boolean checkClear(StringWrapper wrapper) {

        final String line = wrapper.getString();

        final Matcher matcher = CLEAR_PATTERN.matcher(line);
        if (!matcher.matches()) {
            return false;
        }

        final String what = matcher.group(1);
        final Clear[] clears = Clear.forValue(what);
        for (Clear clear: clears) {
            switch (clear) {

                case VARIABLES:
                    isVariable = false;
                    variables.clear();
                    break;

                case METHODS:
                    methods.clear();
                    innerMethodBlocks = 0;
                    indent.setCount(0);
                    isWritingMethod = false;
                    isMethodCompilePhase = false;
                    currentMethod.delete(0, currentMethod.length());
                    break;

                case IMPORTS:
                    imports.clear();
                    break;

                case VOID:
                    if (variables.size() != 0) {
                        final Iterator<String> iterator = variables.iterator();
                        while (iterator.hasNext()) {
                            final String next = iterator.next();
                            if (next.startsWith(VOID)) {
                                iterator.remove();
                            }
                        }
                    }
                    break;
            }
        }

        wrapper.setString(null);

        return true;
    }

    private boolean checkImports(StringWrapper wrapper) {
        final String line = wrapper.getString();
        if (line.startsWith(IMPORT)) {
            imports.add(semicolon(line));
            wrapper.setString(null);
            return true;
        }
        return false;
    }

    private ExecutionState checkMethod(StringWrapper wrapper) {

        final String line = wrapper.getString();

        if (isWritingMethod) {

            final boolean isBlockStart  = isBlockStart(line);
            final boolean isBlockEnd    = isBlockEnd(line);
            if (isBlockStart && isBlockEnd) {
                // don't indent
            } else if (isBlockStart) {
                indent.setCount(++innerMethodBlocks);
            } else if (isBlockEnd) {
                indent.setCount(--innerMethodBlocks);
            }

            currentMethod.append((isBlockEnd || isBlockStart) ? line : semicolon(line));

            if (innerMethodBlocks == 0) {
                endMethodWriting();
                wrapper.setString(null);
            } else {
                System.out.print(indent.toString());
                return ExecutionState.RETURN;
            }

            return ExecutionState.TRUE;

        } else {
            final Matcher matcher = METHOD_PATTERN.matcher(line);
            if (matcher.matches()) {

                currentMethod.append(STATIC)
                        .append(line);

                if (isBlockEnd(line)) {
                    wrapper.setString(null);
                    endMethodWriting();
                    return ExecutionState.TRUE;
                } else {
                    isWritingMethod = true;
                    indent.setCount(++innerMethodBlocks);
                    System.out.print(indent.toString());
                    return ExecutionState.RETURN;
                }
            }
        }

        return ExecutionState.FALSE;
    }

    private ExecutionState checkVariable(StringWrapper wrapper) {

        final String line = wrapper.getString();

        if (line.startsWith(VOID)) {
            isVariable = true;
            variables.add(semicolon(line.substring(VOID.length(), line.length())));
            wrapper.setString(null);
            return ExecutionState.TRUE;
        }

        final Matcher varMatcher = VARIABLE_PATTERN.matcher(line);
        isVariable = varMatcher.matches();
        if (isVariable) {

            variables.add(semicolon(line));

            // extract var name
            final Matcher varNameMatcher = VARIABLE_NAME_PATTERN.matcher(line);
            if (varNameMatcher.matches()) {
                wrapper.setString(varNameMatcher.group(2));
            } else {
                wrapper.setString(null);
            }
        }

        return isVariable ? ExecutionState.TRUE : ExecutionState.FALSE;
    }

    private boolean isBlockStart(String line) {
        final Matcher matcher = BLOCK_START_PATTERN.matcher(line);
        return matcher.matches();
    }

    private boolean isBlockEnd(String line) {
        final Matcher matcher = BLOCK_END_PATTERN.matcher(line);
        return matcher.matches();
    }

    private void endMethodWriting() {
        isWritingMethod = false;
        methods.add(currentMethod.toString());
        currentMethod.delete(0, currentMethod.length());
        isMethodCompilePhase = true;
        innerMethodBlocks = 0;
    }

    private void onCompileError() {
        if (isVariable) { // remove last wrong variable
            variables.remove(variables.size() - 1);
        } else if (isMethodCompilePhase) { // remove last wrong method
            methods.remove(methods.size() - 1);
        }
    }

    private static String semicolon(String in) {
        if (in.charAt(in.length() - 1) != SEMI_COLON) {
            return in + SEMI_COLON;
        }
        return in;
    }

    private void write(List<String> list) {
        if (list.size() == 0) {
            return;
        }
        for (String i: list) {
            writer.println(i);
        }
    }
}
