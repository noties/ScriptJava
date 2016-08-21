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

import java.util.HashMap;
import java.util.Map;

public class Json {

    public static final Object NULL = new Object() { public String toString() { return "null"; } };

    // sometimes it can be just array...
    public static Map<Object, Object> json(String in) {
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

        public Map<Object, Object> read() throws JsonException {

            // main entry point, here: object, array, null
            if (length == 0) {
                return null;
            }

            i = -1;

            Map value;
            Token token;

            while ((token = token()) == Token.WHITESPACE) {}

            switch (token){

                case OBJECT:
                    value = readObject();
                    break;

                case ARRAY:
                    value = readArray();
                    break;

                default:
                    throw new JsonException("Unexpected token %s at %d", token, i);
            }

            //noinspection unchecked
            return value;
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

//            System.out.printf("`%s`, builder: %s, number: %s (%s)%n", c, builder, number, floatingPoint);

            return number;
        }

        public Map<Integer, Object> readArray() throws JsonException {

            final Map<Integer, Object> map = new HashMap<>();

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
                            map.put(index, pendingValue);
                            index += 1;
                            break;
                        }
                        throw new JsonException("Unexpected char `%s` whilst parsing array at %d", c, i);

                    default:
                        pendingValue = readToken(token);
                }

                token = token();
            }

            if (pendingValue != null) {
                map.put(index, pendingValue);
            }

            return map;
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

//                System.out.printf("%s, pendingkey: %s, pendingvalue: %s%n", token, pendingKey, pendingValue);

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
}
