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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatementSubstitution {

    private static final Pattern SUBSTITUTE_MAP = Pattern.compile(".*\\s*map\\((.+)\\).*");
    private static final Pattern SUBSTITUTE_RET = Pattern.compile(".*(=|\\s)*ret\\s*\\((\"*\\w+\"*)\\s*,\\s*(.+)\\).*");

    public static String substitute(String in) {
        final String out;
        {
            final Matcher map = SUBSTITUTE_MAP.matcher(in);
            if (map.matches()) {
                final String toProcess = map.group(1);
                final String[] kv = toProcess.split(",");
                final int length = Length.len(kv);
                final List<String> keys     = new ArrayList<>(length);
                final List<String> values   = new ArrayList<>(length);
                String[] split;
                for (String keyValue: kv) {
                    split = keyValue.split(":");
                    keys.add(split[0].trim());
                    values.add(split[1].trim());
                }
                final StringBuilder builder = new StringBuilder();
                builder.append("new String[] { ");
                boolean first = true;
                for (String key: keys) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append('"')
                            .append(key)
                            .append('"');
                }
                builder.append(" }, new Object[] { ");
                first = true;
                for (String value: values) {
                    if (first) {
                        first = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(value);
                }
                builder.append(" }");
                // execute substitute
                out = in.replace(toProcess, builder.toString());
            } else {

                final Matcher ret = SUBSTITUTE_RET.matcher(in);
                if (ret.matches()) {
//                    final String key = ret.group(1);
                    final String provider = ret.group(3);
                    out = in.replace(provider, String.format("new Provider() { public Object provide() { return %s; } }", provider));
                } else {
                    out = in;
                }
            }
        }

        // ok, we need a real way to check how we can make `;` really optional....
        // and maybe color the code?

        return out;
    }
}
