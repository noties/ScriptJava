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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class Network {

    private static final String HTTP_GET    = "GET";
    private static final String HTTP_POST   = "POST";

    private static final String USER_AGENT_KEY = "User-Agent";
    private static final String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11";

    private static final int REDIRECTS_LIMIT = 5;

    public static byte[] dld(String url) {
        return IO.bytes(http(url, HTTP_GET, null, null));
    }

    public static String get(String url) {
        return Str.str(http(url, HTTP_GET, null, null));
    }

    public static String get(String url, Map<String, ?> properties) {
        return Str.str(http(url, HTTP_GET, null, properties));
    }

    public static String post(String url) {
        return Str.str(http(url, HTTP_POST, null, null));
    }

    public static String post(String url, String body) {
        return Str.str(http(url, HTTP_POST, body, null));
    }

    public static String post(String url, String body, Map<String, ?> properties) {
        return Str.str(http(url, HTTP_POST, body, properties));
    }

    private static InputStream http(String url, String method, String body, Map<String, ?> properties) {
        try {

            // no we follow redirects, but we also need to limit it
            // so, we don't fall in the endless loop

            int redirects = 0;

            String requestUrl = url;
            HttpURLConnection connection = null;

            while (requestUrl != null) {

                final URL u = new URL(requestUrl);

                connection = (HttpURLConnection) u.openConnection();
                connection.setRequestMethod(method);
                connection.setInstanceFollowRedirects(true);

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

                final int responseCode = connection.getResponseCode();
                if (responseCode >= 200 && responseCode < 300) {
                    break;
                } else if (isRedirect(responseCode)) {
                    // check if the redirect url is different from the current one
                    final String location = connection.getHeaderField("Location");
                    if (requestUrl.equals(location)) {
                        throw new RuntimeException(String.format("Url redirects to itself: `%s`", requestUrl));
                    } else {
                        redirects += 1;
                        if (redirects >= REDIRECTS_LIMIT) {
                            throw new RuntimeException("Reached redirects limit");
                        }
                        requestUrl = location;
                    }
                } else {
                    throw new RuntimeException(connection.getResponseMessage() + ", " + responseCode);
                }
            }

            if (Bool.bool(connection)) {
                return connection.getInputStream();
            }

        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }

    private static boolean isRedirect(int responseCode) {
        return 301 == responseCode
                || 302 == responseCode
                || 303 == responseCode
                || 307 == responseCode;
    }

    private Network() {

    }
}
