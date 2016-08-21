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

package scriptjava.file;

import java.util.Set;

public class ScriptFile {

    public static final String FILE_EXTENSION = "sj";

    // #!/scriptjava
    // # @@include
    // # @@import
    // # @@members
    // # @@execution
    // # @@variables ?

    public static ScriptFile parse(String in) {
        return null;
    }

    private final Set<String> includes;
    private final Set<String> imports;
    private final String members;
    private final String execution;

    public ScriptFile(Set<String> includes, Set<String> imports, String members, String execution) {
        this.includes = includes;
        this.imports = imports;
        this.members = members;
        this.execution = execution;
    }

    public String generate() {
        return null;
    }
}
