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

package scriptjava.buildins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Json {

    public static final Object NULL = new Object() { public String toString() { return "null"; } };

    public static abstract class Element {

        // call for an object's property
        public abstract Element key(String key);

        // call for array
        public abstract Element at(int index);

        // return value
        public abstract Object get();
    }

    // sometimes it can be just array...
    public static Element json(String in) {
        return new JsonReader(in).read();
    }

    private Json() {

    }

    // json("{\"ip\": true, \"ip2\": false}"){ip}{ip2}.get(0)

    static class JsonException extends IllegalStateException {
        JsonException(String s, Object... args) {
            super(String.format(s, args));
        }
    }

    public static class JsonReader {

        enum Token {
            OBJECT, ARRAY, BOOLEAN, NUMBER, STRING, NULL, WHITESPACE, EOF, UNKNOWN
        }

        final String input;
        final int length;

        final StringBuilder builder = new StringBuilder();

        int i;
        char c;

        public JsonReader(String input) {
            this.input = input;
            this.length = Length.len(input);
        }

        public Element read() throws JsonException {

            // main entry point, here: object, array, null
            if (length == 0) {
                return null;
            }

            i = -1;

            Map map = null;
            List list = null;

            Token token;

            while ((token = token()) == Token.WHITESPACE) {}

            switch (token){

                case OBJECT:
                    map = readObject();
                    break;

                case ARRAY:
                    list = readArray();
                    break;

                default:
                    throw new JsonException("Unexpected token %s at %d", token, i);
            }

            final Element element;
            if (map != null) {
                //noinspection unchecked
                element = new ElementObject(map);
            } else {
                //noinspection unchecked
                element = new ElementArray(list);
            }

            //noinspection unchecked
            return element;
        }

        public Boolean readBoolean() throws JsonException {

            next();

            final Boolean result;
            final int skip;

            switch (c) {

                case 'r':
                    // true
                    result = Boolean.TRUE;
                    skip = 2;
                    break;

                case 'a':
                    // false
                    result = Boolean.FALSE;
                    skip = 3;
                    break;

                default:
                    throw new JsonException("Unexpected character for boolean value at: %d", i);
            }

            next(skip);

            return result;
        }

        public Object readNull() throws JsonException {
            final Object result = NULL;
            next(2);
            return result;
        }

        public Number readNumber() throws JsonException {

            // return Long for integers & Double for floating point numbers

            builder.setLength(0);

            if ('-' == c) {
                builder.append('-');
                next();
            }

            boolean floatingPoint = false;

            while ((c >= '0' && c <= '9') || (c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E')) {
                if (!floatingPoint && ('.' == c || 'e' == c || 'E' == c)) {
                    floatingPoint = true;
                }
                builder.append(c);
                next();
            }

            next(-1);

            final Number number;
            if (floatingPoint) {
                number = Double.parseDouble(builder.toString());
            } else {
                number = Long.parseLong(builder.toString());
            }

            return number;
        }

        public List<Object> readArray() throws JsonException {

            final List<Object> list = new ArrayList<>();

            int index = 0;
            Object pendingValue = null;

            Token token = token();

            while (c != ']') {
                switch (token) {

                    case WHITESPACE:
                        break;

                    case EOF:
                        throw new JsonException("Unexpected EOF whilst parsing array at %d", i);

                    case UNKNOWN:
                        if (c == ',') {
                            list.add(index, pendingValue);
                            index += 1;
                            pendingValue = null;
                            break;
                        }
                        throw new JsonException("Unexpected char `%s` whilst parsing array at %d", c, i);

                    default:
                        pendingValue = readToken(token);
                }

                token = token();
            }

            if (pendingValue != null) {
                list.add(index, pendingValue);
            }

            return list;
        }

        public Map<String, Object> readObject() throws JsonException {

            final Map<String, Object> map = new HashMap<>();

            String pendingKey = null;
            Object pendingValue = null;

            Token token = token();

            // object delimiter
            while (c != '}') {

                switch (token) {

                    case WHITESPACE:
                        break;

                    case STRING:
                        if (pendingKey == null) {
                            pendingKey = readString();
                        } else {
                            pendingValue = readString();
                        }
                        break;

                    case UNKNOWN:
                        if (',' == c) {
                            map.put(pendingKey, pendingValue);
                            pendingKey = null;
                            pendingValue = null;
                        }
                        break;

                    case EOF:
                        throw new JsonException("Unexpected EOF whilst parsing an object at index: " + i);

                    default:
                        pendingValue = readToken(token);
                }

                token = token();
            }

            if (pendingKey != null) {
                map.put(pendingKey, pendingValue);
            }

            return map;
        }

        private Object readToken(Token token) throws JsonException {

            final Object value;

            switch (token) {

                case OBJECT:
                    value = readObject();
                    break;

                case ARRAY:
                    value = readArray();
                    break;

                case BOOLEAN:
                    value = readBoolean();
                    break;

                case NUMBER:
                    value = readNumber();
                    break;

                case STRING:
                    value = readString();
                    break;

                case NULL:
                    value = readNull();
                    break;

                default:
                    throw new JsonException("Unexpected token: %s", token);
            }

            return value;
        }

        public String readString() throws JsonException {

            next();

            builder.setLength(0);

            while (c != '"') {

                // escaped character
                if (c == '\\') {

                    // read next value
                    next();

                    // it's a unicode char
                    if ('u' == c) {
                        // we take 4 next chars
                        final char[] chars = new char[4];
                        for (int i = 0; i < 4; i++) {
                            next();
                            chars[i] = c;
                        }
                        final char unicode = (char) (Integer.parseInt(new String(chars), 16));
                        builder.append(unicode);
                    } else {
                        final char escaped;
                        switch (c) {
                            case 'b': escaped = '\b'; break;
                            case 't': escaped = '\t'; break;
                            case 'r': escaped = '\r'; break;
                            case 'n': escaped = '\n'; break;
                            case 'f': escaped = '\f'; break;
                            default:
                                escaped = c;
                        }
                        builder.append(escaped);
                    }
                } else {
                    builder.append(c);
                }

                next();
            }

            final String result;
            if (builder.length() == 0) {
                result = null;
            } else {
                result  = builder.toString();
            }

            return result;
        }

        private void next() {
            next(1);
        }

        private void next(int step) {
            i += step;
            c = input.charAt(i);
        }

        private Token token() {

            final Token token;

            // let's check if we can parse next...
            if ((i + 1) >= length) {
                token = Token.EOF;
            } else {

                next();

                if (Character.isWhitespace(c)) {
                    token = Token.WHITESPACE;
                } else {
                    switch (c) {

                        case '"': token = Token.STRING; break;
                        case '{': token = Token.OBJECT; break;
                        case '[': token = Token.ARRAY; break;
                        case 'n': token = Token.NULL; break;

                        case 't':
                        case 'f':
                            token = Token.BOOLEAN;
                            break;

                        default:
                            // okay, here we check it' a number
                            if ('-' == c || (c >= '0' && c <='9')) {
                                token = Token.NUMBER;
                            } else {
                                token = Token.UNKNOWN;
                            }
                    }
                }
            }

            return token;
        }
    }

    private static Element newElementInstance(Object o) {
        final Element element;
        if (o == null || NULL == o) {
            element = new Json.ElementValue(NULL);
        } else if (o instanceof Map) {
            //noinspection unchecked
            element = new ElementObject((Map<String, Object>) o);
        } else if (o instanceof List) {
            //noinspection unchecked
            element = new ElementArray((List<Object>) o);
        } else {
            element = new ElementValue(o);
        }
        return element;
    }

    private static class ElementObject extends Element {

        private final Map<String, Object> map;

        ElementObject(Map<String, Object> map) {
            this.map = map;
        }

        @Override
        public Element key(String key) {
            return newElementInstance(map.get(key));
        }

        @Override
        public Element at(int index) {
            throw new JsonException("Not an JSON array -> a JSON object");
        }

        @Override
        public Object get() {
            throw new JsonException("Not a JSON value -> a JSON object");
        }

        @Override
        public String toString() {
            return Str.str(map);
        }
    }

    private static class ElementArray extends Element {

        private final List<Object> array;

        ElementArray(List<Object> array) {
            this.array = array;
        }

        @Override
        public Element key(String key) {
            throw new JsonException("Not a JSON object -> a JSON array");
        }

        @Override
        public Element at(int index) {
            return newElementInstance(array.get(index));
        }

        @Override
        public Object get() {
            throw new JsonException("Not a JSON object -> a JSON array");
        }

        @Override
        public String toString() {
            return Str.str(array);
        }
    }

    private static class ElementValue extends Element {

        private final Object value;

        ElementValue(Object value) {
            this.value = value;
        }

        @Override
        public Element key(String key) {
            throw new JsonException("Not a JSON object -> a JSON value");
        }

        @Override
        public Element at(int index) {
            throw new JsonException("Not a JSON array -> a JSON value");
        }

        @Override
        public Object get() {
            return value;
        }

        @Override
        public String toString() {
            return Str.str(value);
        }
    }
}
