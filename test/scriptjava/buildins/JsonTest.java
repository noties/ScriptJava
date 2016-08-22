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

import com.google.gson.*;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonTest {

    // json is taken from json.org/examples

    @Test
    public void testEmptyWithTypes() {
        // we support only objects and arrays as root
        check("{}");
        check("[]");
    }

    @Test
    public void testSimpleArray() {
        check("[0, 1, 2, 3, 4, 5, 6, 7, 8, 9]");
    }

    @Test
    public void testArrayOfNulls() {
        check("[null, null, null, null, null]");
    }

    // json taken from: http://json.org/example.html
    @Test
    public void testPreparedJson_01() {
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
        check(json);
    }

    @Test
    public void testPreparedJson_02() {
        final String json = "{\n" +
                "  \"menu\": {\n" +
                "    \"id\": \"file\",\n" +
                "    \"value\": \"File\",\n" +
                "    \"popup\": {\n" +
                "      \"menuitem\": [\n" +
                "        {\n" +
                "          \"value\": \"New\",\n" +
                "          \"onclick\": \"CreateNewDoc()\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Open\",\n" +
                "          \"onclick\": \"OpenDoc()\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"value\": \"Close\",\n" +
                "          \"onclick\": \"CloseDoc()\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  }\n" +
                "}";
        check(json);
    }

    @Test
    public void testPreparedJson_03() {
        final String json = "{\n" +
                "  \"widget\": {\n" +
                "    \"debug\": \"on\",\n" +
                "    \"window\": {\n" +
                "      \"title\": \"Sample Konfabulator Widget\",\n" +
                "      \"name\": \"main_window\",\n" +
                "      \"width\": 500,\n" +
                "      \"height\": 500\n" +
                "    },\n" +
                "    \"image\": {\n" +
                "      \"src\": \"Images/Sun.png\",\n" +
                "      \"name\": \"sun1\",\n" +
                "      \"hOffset\": 250,\n" +
                "      \"vOffset\": 250,\n" +
                "      \"alignment\": \"center\"\n" +
                "    },\n" +
                "    \"text\": {\n" +
                "      \"data\": \"Click Here\",\n" +
                "      \"size\": 36,\n" +
                "      \"style\": \"bold\",\n" +
                "      \"name\": \"text1\",\n" +
                "      \"hOffset\": 250,\n" +
                "      \"vOffset\": 100,\n" +
                "      \"alignment\": \"center\",\n" +
                "      \"onMouseUp\": \"sun1.opacity = (sun1.opacity / 100) * 90;\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        check(json);
    }

    @Test
    public void testPreparedJson_04() {
        final String json = "{\n" +
                "  \"web-app\": {\n" +
                "    \"servlet\": [\n" +
                "      {\n" +
                "        \"servlet-name\": \"cofaxCDS\",\n" +
                "        \"servlet-class\": \"org.cofax.cds.CDSServlet\",\n" +
                "        \"init-param\": {\n" +
                "          \"configGlossary:installationAt\": \"Philadelphia, PA\",\n" +
                "          \"configGlossary:adminEmail\": \"ksm@pobox.com\",\n" +
                "          \"configGlossary:poweredBy\": \"Cofax\",\n" +
                "          \"configGlossary:poweredByIcon\": \"/images/cofax.gif\",\n" +
                "          \"configGlossary:staticPath\": \"/content/static\",\n" +
                "          \"templateProcessorClass\": \"org.cofax.WysiwygTemplate\",\n" +
                "          \"templateLoaderClass\": \"org.cofax.FilesTemplateLoader\",\n" +
                "          \"templatePath\": \"templates\",\n" +
                "          \"templateOverridePath\": \"\",\n" +
                "          \"defaultListTemplate\": \"listTemplate.htm\",\n" +
                "          \"defaultFileTemplate\": \"articleTemplate.htm\",\n" +
                "          \"useJSP\": false,\n" +
                "          \"jspListTemplate\": \"listTemplate.jsp\",\n" +
                "          \"jspFileTemplate\": \"articleTemplate.jsp\",\n" +
                "          \"cachePackageTagsTrack\": 200,\n" +
                "          \"cachePackageTagsStore\": 200,\n" +
                "          \"cachePackageTagsRefresh\": 60,\n" +
                "          \"cacheTemplatesTrack\": 100,\n" +
                "          \"cacheTemplatesStore\": 50,\n" +
                "          \"cacheTemplatesRefresh\": 15,\n" +
                "          \"cachePagesTrack\": 200,\n" +
                "          \"cachePagesStore\": 100,\n" +
                "          \"cachePagesRefresh\": 10,\n" +
                "          \"cachePagesDirtyRead\": 10,\n" +
                "          \"searchEngineListTemplate\": \"forSearchEnginesList.htm\",\n" +
                "          \"searchEngineFileTemplate\": \"forSearchEngines.htm\",\n" +
                "          \"searchEngineRobotsDb\": \"WEB-INF/robots.db\",\n" +
                "          \"useDataStore\": true,\n" +
                "          \"dataStoreClass\": \"org.cofax.SqlDataStore\",\n" +
                "          \"redirectionClass\": \"org.cofax.SqlRedirection\",\n" +
                "          \"dataStoreName\": \"cofax\",\n" +
                "          \"dataStoreDriver\": \"com.microsoft.jdbc.sqlserver.SQLServerDriver\",\n" +
                "          \"dataStoreUrl\": \"jdbc:microsoft:sqlserver://LOCALHOST:1433;DatabaseName=goon\",\n" +
                "          \"dataStoreUser\": \"sa\",\n" +
                "          \"dataStorePassword\": \"dataStoreTestQuery\",\n" +
                "          \"dataStoreTestQuery\": \"SET NOCOUNT ON;select test='test';\",\n" +
                "          \"dataStoreLogFile\": \"/usr/local/tomcat/logs/datastore.log\",\n" +
                "          \"dataStoreInitConns\": 10,\n" +
                "          \"dataStoreMaxConns\": 100,\n" +
                "          \"dataStoreConnUsageLimit\": 100,\n" +
                "          \"dataStoreLogLevel\": \"debug\",\n" +
                "          \"maxUrlLength\": 500\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"servlet-name\": \"cofaxEmail\",\n" +
                "        \"servlet-class\": \"org.cofax.cds.EmailServlet\",\n" +
                "        \"init-param\": {\n" +
                "          \"mailHost\": \"mail1\",\n" +
                "          \"mailHostOverride\": \"mail2\"\n" +
                "        }\n" +
                "      },\n" +
                "      {\n" +
                "        \"servlet-name\": \"cofaxAdmin\",\n" +
                "        \"servlet-class\": \"org.cofax.cds.AdminServlet\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"servlet-name\": \"fileServlet\",\n" +
                "        \"servlet-class\": \"org.cofax.cds.FileServlet\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"servlet-name\": \"cofaxTools\",\n" +
                "        \"servlet-class\": \"org.cofax.cms.CofaxToolsServlet\",\n" +
                "        \"init-param\": {\n" +
                "          \"templatePath\": \"toolstemplates/\",\n" +
                "          \"log\": 1,\n" +
                "          \"logLocation\": \"/usr/local/tomcat/logs/CofaxTools.log\",\n" +
                "          \"logMaxSize\": \"\",\n" +
                "          \"dataLog\": 1,\n" +
                "          \"dataLogLocation\": \"/usr/local/tomcat/logs/dataLog.log\",\n" +
                "          \"dataLogMaxSize\": \"\",\n" +
                "          \"removePageCache\": \"/content/admin/remove?cache=pages&id=\",\n" +
                "          \"removeTemplateCache\": \"/content/admin/remove?cache=templates&id=\",\n" +
                "          \"fileTransferFolder\": \"/usr/local/tomcat/webapps/content/fileTransferFolder\",\n" +
                "          \"lookInContext\": 1,\n" +
                "          \"adminGroupID\": 4,\n" +
                "          \"betaServer\": true\n" +
                "        }\n" +
                "      }\n" +
                "    ],\n" +
                "    \"servlet-mapping\": {\n" +
                "      \"cofaxCDS\": \"/\",\n" +
                "      \"cofaxEmail\": \"/cofaxutil/aemail/*\",\n" +
                "      \"cofaxAdmin\": \"/admin/*\",\n" +
                "      \"fileServlet\": \"/static/*\",\n" +
                "      \"cofaxTools\": \"/tools/*\"\n" +
                "    },\n" +
                "    \"taglib\": {\n" +
                "      \"taglib-uri\": \"cofax.tld\",\n" +
                "      \"taglib-location\": \"/WEB-INF/tlds/cofax.tld\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        check(json);
    }

    @Test
    public void testPreparedJson_05() {
        final String json = "{\"menu\": {\n" +
                "    \"header\": \"SVG Viewer\",\n" +
                "    \"items\": [\n" +
                "        {\"id\": \"Open\"},\n" +
                "        {\"id\": \"OpenNew\", \"label\": \"Open New\"},\n" +
                "        null,\n" +
                "        {\"id\": \"ZoomIn\", \"label\": \"Zoom In\"},\n" +
                "        {\"id\": \"ZoomOut\", \"label\": \"Zoom Out\"},\n" +
                "        {\"id\": \"OriginalView\", \"label\": \"Original View\"},\n" +
                "        null,\n" +
                "        {\"id\": \"Quality\"},\n" +
                "        {\"id\": \"Pause\"},\n" +
                "        {\"id\": \"Mute\"},\n" +
                "        null,\n" +
                "        {\"id\": \"Find\", \"label\": \"Find...\"},\n" +
                "        {\"id\": \"FindAgain\", \"label\": \"Find Again\"},\n" +
                "        {\"id\": \"Copy\"},\n" +
                "        {\"id\": \"CopyAgain\", \"label\": \"Copy Again\"},\n" +
                "        {\"id\": \"CopySVG\", \"label\": \"Copy SVG\"},\n" +
                "        {\"id\": \"ViewSVG\", \"label\": \"View SVG\"},\n" +
                "        {\"id\": \"ViewSource\", \"label\": \"View Source\"},\n" +
                "        {\"id\": \"SaveAs\", \"label\": \"Save As\"},\n" +
                "        null,\n" +
                "        {\"id\": \"Help\"},\n" +
                "        {\"id\": \"About\", \"label\": \"About Adobe CVG Viewer...\"}\n" +
                "    ]\n" +
                "}}";
        check(json);
    }

    private static void check(String in) {
        assertElements(new JsonParser().parse(in), Json.json(in));
    }

    private static void assertElements(JsonElement gson, Json.Element ours) {
        assertElements(null, gson, ours);
    }

    private static void assertElements(String parent, JsonElement gson, Json.Element ours) {

        // first obtain type & immediately check for equals
        final Type type;
        {
            type  = type(gson);
            assertEquals(type, type(ours));
        }

        // now, check if we have children and recursively call this method
        switch (type) {

            case PRIMITIVE:
                // we have special case: we don't have NULL type, but gson has
                // we need to check if it's NULL
                if (gson.isJsonNull()) {
                    assertTrue(parent, ours.get() == Json.NULL);
                } else {

                    final Object ourValue = ours.get();

                    // we have 3 primitive types: String, Number & Boolean
                    final JsonPrimitive primitive = (JsonPrimitive) gson;
                    final Object gsonPrimitive;
                    if (primitive.isString()) {
                        gsonPrimitive = primitive.getAsString();
                    } else if (primitive.isNumber()) {
                        // we support only two Numbers: Long & Double
                        // let's rely on that
                        if (ourValue instanceof Double) {
                            gsonPrimitive = primitive.getAsDouble();
                        } else {
                            gsonPrimitive = primitive.getAsLong();
                        }
                    } else {
                        gsonPrimitive = primitive.getAsBoolean();
                    }
                    assertEquals(parent, gsonPrimitive, ourValue);
                }
                break;

            case ARRAY:
                // in case of array -> let's recursively call this method on each element
                final JsonArray jsonArray = gson.getAsJsonArray();
                final int size = jsonArray.size();
                for (int i = 0; i < size; i++) {
                    assertElements(null, jsonArray.get(i), ours.at(i));
                }
                break;

            case OBJECT:
                // in case of object -> let's also recursively call this method on each element
                final JsonObject object = gson.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry: object.entrySet()) {
                    assertElements(entry.getKey(), entry.getValue(), ours.key(entry.getKey()));
                }
                break;
        }
    }


    enum Type {
        OBJECT, ARRAY, PRIMITIVE
    }

    private static Type type(JsonElement element) {
        final Type type;
        // in our build-in we don't distinguish NULL as a standalone type
        if (element.isJsonNull() || element.isJsonPrimitive()) {
            type = Type.PRIMITIVE;
        } else if (element.isJsonArray()) {
            type = Type.ARRAY;
        } else {
            type = Type.OBJECT;
        }
        return type;
    }

    private static Type type(Json.Element element) {
        final Type type;
        if (element instanceof Json.ElementObject) {
            type = Type.OBJECT;
        } else if (element instanceof Json.ElementArray) {
            type = Type.ARRAY;
        } else {
            type = Type.PRIMITIVE;
        }
        return type;
    }
}