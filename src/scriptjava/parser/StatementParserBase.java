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

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public abstract class StatementParserBase implements StatementParser {

    final Set<String> buildIns;

    public StatementParserBase(Class<?>[] buildIns) {
        this.buildIns = buildIns(buildIns);
    }

    static Set<String> buildIns(Class<?>[] buildIns) {
        final Set<String> set = new HashSet<>();
        for (Class<?> buildIn : buildIns) {
            final Method[] methods = buildIn.getMethods();
            for (Method method: methods) {
                set.add(method.getName());
            }
        }
        return set;
    }
}
