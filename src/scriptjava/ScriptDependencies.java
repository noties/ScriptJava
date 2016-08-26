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

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ScriptDependencies {

    private static final String OUTPUT_FOLDER = "-o";
    private static final String MAVEN_LATEST_VERSION = "+";
    private static final String MAVEN_SEARCH_URL = "http://search.maven.org/solrsearch/select?q=";
    private static final String MAVEN_DOWNLOAD_URL = "http://search.maven.org/remotecontent?filepath=";
    private static final String LIBS_FOLDER = "libs";

    private final File executionFolder;
    private final Set<String> addedArtifacts;

    ScriptDependencies(File executionFolder) {
        this.executionFolder = executionFolder;
        this.addedArtifacts = new HashSet<>();

        // we need to put our execution folder to addedDependencies right away
        addedArtifacts.add(executionFolder.getPath() + File.separator + "*");

        // immediately add to classpath our libs folder
        {
            final File file = new File(executionFolder, LIBS_FOLDER);
            if (file.exists()) {
                addToClassPath(file);
            }
        }
    }

    List<String> dependencies() {
        return new ArrayList<>(addedArtifacts);
    }

    // ??? if called with a single char '.' -> just updates the ???
    // accepts a statement & optional arguments
    //
    // arguments:
    //      -o (output folder to copy the artifact) (if not specified the `libs` folder will be used)
    //      ?? -cp (copy the supplied artifact to the output folder), in case of maven dependency is by default
    // statements:
    //      can be a file statement, eg "my.jar", "dl/my.jar", 'di/*' (everything from folder di)
    //      can be a maven dependency: "ru.noties.debug:debug:2.0.2" (the only accepted packaging is `jar`)
    //          "ru.noties.debug:debug:+" -> downloads the latest version
    //
    // compile -o 'mv/in' ru.noties.debug:2.0.2
    // compile dl/*
    //
    void updateDependencies(String statement) {

        // we can have an optional argument that specifies a folder to save our dependencies
        // if possible we check if that artifact already exists (in already added to the classpath)
        // or if it just exists already

        final Reader reader = new Reader(new Reader.Callbacks() {
            @Override
            public void onComplete(Arguments arguments, Data data) {

                // first we will check if data is present
                if (data == null) {
                    return;
                }

                switch (data.type()) {

                    case MAVEN:
                        handleMavenData(arguments, (MavenData) data);
                        break;

                    case FILE:
                        handleFileData(arguments, (FileData) data);
                        break;

                    default:
                        throw new IllegalStateException("Unknown data type: " + data.type());
                }
            }
        });
        reader.read(statement);
    }

    private void handleMavenData(Arguments arguments, MavenData mavenData) {

        // ?? okay, check if we have the full version
        // ?? if we have -> check if we have it in our cache
        // if the version is `+` -> no cache

        try {

            final String version;
            {
                String artifactVersion = null;
                final String json = Network.get(createMavenSearchUrl(mavenData));
                if (json != null) {
                    final Json.Element element = Json.json(json);
                    final Json.Element response = element.key("response");
                    if (response != Json.NULL) {
                        final int found = ((Number) response.key("numFound").get()).intValue();
                        if (found == 0) {
                            throw new RuntimeException(String.format(
                                    "Cannot find artifact. group: `%s`, artifact: `%s`, version: `%s`",
                                    mavenData.group, mavenData.artifact, mavenData.version
                            ));
                        }
                        final int index = ((Number) response.key("start").get()).intValue();
                        final Json.Element doc = response.key("docs").at(index);
                        if (MAVEN_LATEST_VERSION.equals(mavenData.version)) {
                            artifactVersion = (String) doc.key("latestVersion").get();
                        } else {
                            artifactVersion = mavenData.version;
                        }
                    }
                }
                // if at this point we have no artifactVersion -> just return
                if (Bool.bool(artifactVersion)) {
                    version = artifactVersion;
                } else {
                    return;
                }
            }

            // okay at this point we can check if the file is present already
            // but maybe we should not.. just download and put what requested
            final File artifact = file(arguments, artifactName(mavenData.group, mavenData.artifact, version));
            if (!artifact.exists()) {

                // next generate the download url
                final String downloadUrl = createMavenDownloadUrl(mavenData.group, mavenData.artifact, version);

                final byte[] bytes = Network.dld(downloadUrl);

                if (Bool.bool(bytes) && IO.write(artifact, bytes)) {
                    // here we need to add our new artifact to the classpath
                    addToClassPath(artifact);
                }
            } else {
                addToClassPath(artifact);
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void handleFileData(Arguments arguments, FileData fileData) {

        final File file = file(arguments, fileData.file);
        if (file.exists()) {
            addToClassPath(file);
        }
    }

    private File file(Arguments arguments, String fileName) {

        // if file is absolute -> do nothing just add it (no need to look at the arguments)
        // else check if we have arguments -> use it as a parent folder, else `libs`
        final File file = new File(fileName);
        if (file.isAbsolute()) {
            return file;
        }

        // so, we are not absolute
        // detect parent
        final File parent;
        if (arguments != null) {
            // here thing could be a bit hard again -> we need to check if supplied in arguments
            // folder is absolute also, otherwise use execution folder as parent for it
            final File arg = new File(arguments.outputFolder);
            if (arg.isAbsolute()) {
                parent = arg;
            } else {
                parent = new File(executionFolder, arguments.outputFolder);
            }
        } else {
            // if it's a jar -> `libs`
            // otherwise -> executionFolder
            if (fileName.endsWith(".jar")) {
                parent = new File(executionFolder, LIBS_FOLDER);
            } else {
                parent = executionFolder;
            }
        }

        if (!parent.exists()) {
            if (!parent.mkdirs()) {
                throw new RuntimeException("Cannot create folder: " + parent.getPath());
            }
        }

        return new File(parent, fileName);
    }

    private static String artifactName(String group, String artifact, String version) {
        return group + "-"
                + artifact + "-"
                + version + ".jar";
    }

    static String createMavenSearchUrl(MavenData data) {
        final String parameters;
        {
            final StringBuilder builder = new StringBuilder();
            builder.append("g:\"")
                    .append(data.group)
                    .append("\" AND a: \"")
                    .append(data.artifact)
                    .append("\" AND p:\"jar\"");
            if (!MAVEN_LATEST_VERSION.equals(data.version)) {
                builder.append(" AND v:\"")
                        .append(data.version)
                        .append("\"");
            }
            parameters = Buildins.enc(builder.toString());
        }
        return MAVEN_SEARCH_URL + parameters;
    }

    static String createMavenDownloadUrl(String group, String artifact, String version) {
        return MAVEN_DOWNLOAD_URL
                + group.replaceAll("\\.", "/") + "/"
                + artifact + "/"
                + version + "/"
                + artifact + "-"
                + version + ".jar";
    }

    private void addToClassPath(File file) {

        // we need to check if our classloader is capable of adding dependencies
        final URLClassLoader loader;
        {
            final ClassLoader classLoader = getClass().getClassLoader();
            if (classLoader instanceof URLClassLoader) {
                loader = (URLClassLoader) classLoader;
            } else {
                loader = null;
            }
        }

        if (loader == null) {
            return;
        }

        if (file.isDirectory()) {

            // get all the jars inside
            final File[] files = file.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".jar");
                }
            });
            if (Bool.bool(files)) {
                String path;
                for (File jar: files) {
                    path = jar.getPath();
                    if (!addedArtifacts.contains(path)) {
                        addToClassPath(loader, jar);
                        addedArtifacts.add(path);
                    }
                }
            }
        } else {
            // check if we already have it our classpath
            final String path = file.getPath();
            if (!addedArtifacts.contains(path)) {
                addToClassPath(loader, file);
                addedArtifacts.add(path);
            }
        }
    }

    private static void addToClassPath(URLClassLoader loader, File file) {
        try {
            URL_CLASS_LOADER_ADD_TO_PATH.invoke(loader, file.toURI().toURL());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void log(String message, Object... args) {
        System.out.printf(message, args);
        System.out.println();
    }

    static class Reader {

        private static final Pattern MAVEN_REG_EXP = Pattern.compile("(.+):(\\w+):(\\+|.+)");

        interface Callbacks {
            void onComplete(Arguments arguments, Data data);
        }

        private final Callbacks callbacks;

        Reader(Callbacks callbacks) {
            this.callbacks = callbacks;
        }

        void read(String statement) {

            if (Bool.bool(statement)) {

                final List<String> parameters = parseParameters(statement);

                if (Bool.bool(parameters)) {

                    // okay, we have our parameters, let's parse them
                    // first let's start with arguments
                    final Arguments arguments = arguments(parameters);

                    // after arguments we have only one item (if any)
                    if (Bool.bool(parameters)) {

                        // if there will be other parameters we will just ignore them
                        final Data data;
                        final String what = parameters.get(0);
                        final Matcher matcher = MAVEN_REG_EXP.matcher(what);
                        if (matcher.matches()) {
                            data = new MavenData(matcher.group(1), matcher.group(2), matcher.group(3));
                        } else {
                            data = new FileData(what);
                        }

                        callbacks.onComplete(arguments, data);
                        return;
                    }
                }
            }

            // nothing here
            // will just notify listener with all null values
            callbacks.onComplete(null, null);
        }

        // modifies the input string (if arguments were found they will be removed from the list)
        static Arguments arguments(List<String> list) {
            final Iterator<String> iterator = list.iterator();
            String next;
            if (iterator.hasNext()) {
                next = iterator.next();
                if (OUTPUT_FOLDER.equals(next)) {
                    iterator.remove();
                    if (iterator.hasNext()) {
                        next = iterator.next();
                        iterator.remove();
                        return new Arguments(next);
                    }
                }
            }
            return null;
        }

        static List<String> parseParameters(String statement) {

            final int length = Length.len(statement);
            final List<String> parameters = new ArrayList<>();
            final StringBuilder builder = new StringBuilder();

            char c;
            char delimiter = 0;
            boolean parsing = false;
            boolean isWhiteSpace;

            // ignore whitespaces
            // if met delimiter, store it, but go to next (will be used as a end token for parameter)
            // if didn't met delimiter, but found a char or a `-` -> start parsing till the next whitespace (otherwise) till the next delimiter
            for (int i = 0; i < length; i++) {

                c = statement.charAt(i);
                isWhiteSpace = Character.isWhitespace(c);

                // skip whitespaces if not in parsing state -> otherwise just add
                if (isWhiteSpace) {
                    // if not parsing -> skip
                    if (parsing) {
                        // if we have delimiter (aka != '\0') -> just add
                        // if not -> we have found the end
                        if (delimiter != 0) {
                            builder.append(c);
                        } else {
                            if (Bool.bool(builder)) {
                                parameters.add(builder.toString());
                                builder.setLength(0);
                            }
                            parsing = false;
                            delimiter = 0;
                        }
                    } else {
                        parsing = false;
                    }
                } else {
                    // can be our delimiter, `-`, or just a whitespace
                    if (c == '\"' || c == '\'') {
                        // here we equally could be starting parsing and ending
                        if (parsing) {
                            if (Bool.bool(builder)) {
                                parameters.add(builder.toString());
                                builder.setLength(0);
                            }
                            delimiter = 0;
                            parsing = false;
                        } else {
                            delimiter = c;
                            parsing = true;
                        }
                    } else {
                        if (!parsing) {
                            parsing = true;
                        }
                        builder.append(c);
                    }
                }
            }

            if (builder.length() > 0) {
                parameters.add(builder.toString());
            }

            return parameters;
        }
    }


    private static final Method URL_CLASS_LOADER_ADD_TO_PATH;
    static {
        Method method;
        try {
            method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
        } catch (Throwable t) {
            t.printStackTrace();
            method = null;
        }
        URL_CLASS_LOADER_ADD_TO_PATH = method;
    }

    static class Arguments {

        final String outputFolder;

        Arguments(String outputFolder) {
            this.outputFolder = outputFolder;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Arguments arguments = (Arguments) o;

            return outputFolder != null ? outputFolder.equals(arguments.outputFolder) : arguments.outputFolder == null;

        }

        @Override
        public int hashCode() {
            return outputFolder != null ? outputFolder.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "Arguments{" +
                    "outputFolder='" + outputFolder + '\'' +
                    '}';
        }
    }


    enum DataType {
        MAVEN, FILE
    }

    interface Data {
        DataType type();
    }

    static class MavenData implements Data {

        final String group;
        final String artifact;
        final String version;

        MavenData(String group, String artifact, String version) {
            this.group = group;
            this.artifact = artifact;
            this.version = version;
        }

        @Override
        public DataType type() {
            return DataType.MAVEN;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MavenData data = (MavenData) o;

            if (group != null ? !group.equals(data.group) : data.group != null) return false;
            if (artifact != null ? !artifact.equals(data.artifact) : data.artifact != null) return false;
            return version != null ? version.equals(data.version) : data.version == null;

        }

        @Override
        public int hashCode() {
            int result = group != null ? group.hashCode() : 0;
            result = 31 * result + (artifact != null ? artifact.hashCode() : 0);
            result = 31 * result + (version != null ? version.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MavenData{" +
                    "group='" + group + '\'' +
                    ", artifact='" + artifact + '\'' +
                    ", version='" + version + '\'' +
                    '}';
        }
    }

    static class FileData implements Data {

        final String file;

        FileData(String file) {
            this.file = file;
        }

        @Override
        public DataType type() {
            return DataType.FILE;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FileData fileData = (FileData) o;

            return file != null ? file.equals(fileData.file) : fileData.file == null;

        }

        @Override
        public int hashCode() {
            return file != null ? file.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "FileData{" +
                    "file='" + file + '\'' +
                    '}';
        }
    }
}
