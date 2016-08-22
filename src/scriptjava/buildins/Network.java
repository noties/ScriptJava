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

package scriptjava.buildins;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Network {

    private static final String HTTP_GET    = "GET";
    private static final String HTTP_POST   = "POST";

    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    public static String get(String url) {
        return http(url, HTTP_GET, null, null);
    }

    public static String get(String url, Map<String, ?> properties) {
        return http(url, HTTP_GET, null, properties);
    }

    public static String post(String url) {
        return http(url, HTTP_POST, null, null);
    }

    public static String post(String url, String body) {
        return http(url, HTTP_POST, body, null);
    }

    public static String post(String url, String body, Map<String, ?> properties) {
        return http(url, HTTP_POST, body, properties);
    }

    private static String http(String url, String method, String body, Map<String, ?> properties) {
        try {

            final URL u = new URL(url);
            final HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setRequestMethod(method);

            if (Bool.bool(properties)) {

                if (!properties.containsKey(USER_AGENT_KEY)) {
                    connection.setRequestProperty(USER_AGENT_KEY, USER_AGENT_VALUE);
                }

                for (Map.Entry<String, ?> entry: properties.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), String.valueOf(entry.getValue()));
                }
            } else {
                connection.setRequestProperty(USER_AGENT_KEY, USER_AGENT_VALUE);
            }

            if (HTTP_POST.equals(method)) {
                // should we throw here? post requires a body, todo
                if (Bool.bool(body)) {
//                    connection.setRequestProperty("Content-Length", String.valueOf(body.getBytes().length));
                    connection.setDoOutput(true);
                    final DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes(body);
                    outputStream.flush();
                }
            }

            return Str.str(connection.getInputStream());

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    private Network() {

    }
}
