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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Storage {

    public interface Provider {
        Object provide();
    }

    private static final Map<String, Object> MAP = Collections.synchronizedMap(new HashMap<String, Object>());

    public static Object store(String key, Object value) {
        if (!Bool.bool(value)) {
            return MAP.remove(key);
        } else {
            return MAP.put(key, value);
        }
    }

    public static <T> T ret(String key, Provider provider) {
        //noinspection unchecked
        T value = (T) MAP.get(key);
        if (value == null) {
            //noinspection unchecked
            value = (T) provider.provide();
            MAP.put(key, value);
        }
        return value;
    }

    private Storage() {

    }
}
