package scriptjava.buildins;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Дмитрий on 17.08.2016.
 */
public class JsonTest {

    // json is taken from json.org/examples

    @Test
    public void test_01() {
        final String json = "{\n" +
                "  \"glossary\": {\n" +
                "    \"title\": \"example glossary\",\n" +
                "    \"GlossDiv\": {\n" +
                "      \"title\": \"S\",\n" +
                "      \"GlossList\": {\n" +
                "        \"GlossEntry\": {\n" +
                "          \"ID\": \"SGML\",\n" +
                "          \"SortAs\": \"SGML\",\n" +
                "          \"GlossTerm\": \"Standard Generalized Markup Language\",\n" +
                "          \"Acronym\": \"SGML\",\n" +
                "          \"Abbrev\": \"ISO 8879:1986\",\n" +
                "          \"GlossDef\": {\n" +
                "            \"para\": \"A meta-markup language, used to create markup languages such as DocBook.\",\n" +
                "            \"GlossSeeAlso\": [\n" +
                "              \"GML\",\n" +
                "              \"XML\"\n" +
                "            ]\n" +
                "          },\n" +
                "          \"GlossSee\": \"markup\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
    }

    @Test
    public void test_02() {

    }

    private static void check(String json) {
        final Map<Object, Object> map = Json.json(json);
        // convert to string again
        // remove all \t, \n, whitespaces -> check for equality
    }
}