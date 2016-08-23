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

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static scriptjava.buildins.Bool.bool;

public class BoolTest {

//    @Test
//    public void testBool() throws Exception {
//        assertEquals(false, bool());
//    }

    @Test
    public void testBoolCharSequence() throws Exception {
        assertEquals(false, bool((String) null));
        assertEquals(false, bool(""));
        assertEquals(true, bool("not empty"));
    }

    @Test
    public void testBoolCollection() throws Exception {
        assertEquals(false, bool((Collection<?>) null));
        assertEquals(false, bool(Collections.EMPTY_LIST));
        assertEquals(false, bool(Collections.EMPTY_SET));
        assertEquals(true, bool(Arrays.asList(false, false, false)));
    }

    @Test
    public void testBoolMap() throws Exception {
        assertEquals(false, bool((Map<?, ?>) null));
        assertEquals(false, bool(Collections.EMPTY_MAP));
        assertEquals(true, bool(Collections.singletonMap("false", false)));
    }

    @Test
    public void testBoolObjectArray() throws Exception {
        assertEquals(false, bool((Object[]) null));
        assertEquals(false, bool(new Object[0]));
        assertEquals(true, bool(new Object[] { false }));
    }

    @Test
    public void testBoolBoolean() throws Exception {
        assertEquals(false, bool((Boolean) null));
        assertEquals(false, bool(false));
        assertEquals(true, bool(true));
        assertEquals(true, bool(Boolean.TRUE));
    }

    @Test
    public void testBoolByte() throws Exception {
        assertEquals(false, bool((byte) 0));
        assertEquals(false, bool((Byte) null));
        assertEquals(true, bool((byte) 1));
        assertEquals(true, bool(Byte.valueOf((byte) 1)));
    }

    @Test
    public void testBoolShort() throws Exception {
        assertEquals(false, bool((short) 0));
        assertEquals(false, bool((Short) null));
        assertEquals(true, bool((short) 1));
        assertEquals(true, bool(Short.valueOf((short) 1)));
    }

    @Test
    public void testBoolInteger() throws Exception {
        assertEquals(false, bool(0));
        assertEquals(false, bool((Integer) null));
        assertEquals(true, bool(1));
        assertEquals(true, bool(Integer.valueOf(1)));
    }

    @Test
    public void testBoolLong() throws Exception {
        assertEquals(false, bool(0L));
        assertEquals(false, bool((Long) null));
        assertEquals(true, bool(1L));
        assertEquals(true, bool(Long.valueOf(1L)));
    }

    @Test
    public void testBoolFloat() throws Exception {
        assertEquals(false, bool(.0F));
        assertEquals(false, bool((Float) null));
        assertEquals(true, bool(1.F));
        assertEquals(true, bool(Float.valueOf(1.F)));
    }

    @Test
    public void testBoolDouble() throws Exception {
        assertEquals(false, bool(.0D));
        assertEquals(false, bool((Double) null));
        assertEquals(true, bool(1.D));
        assertEquals(true, bool(Double.valueOf(1.D)));
    }

    @Test
    public void testBoolBooleanArray() throws Exception {
        assertEquals(false, bool((boolean[]) null));
        assertEquals(false, bool(new boolean[0]));
        assertEquals(true, bool(new boolean[1]));
    }

    @Test
    public void testBoolByteArray() throws Exception {
        assertEquals(false, bool((byte[]) null));
        assertEquals(false, bool(new byte[0]));
        assertEquals(true, bool(new byte[1]));
    }

    @Test
    public void testBoolShortArray() throws Exception {
        assertEquals(false, bool((short[]) null));
        assertEquals(false, bool(new short[0]));
        assertEquals(true, bool(new short[1]));
    }

    @Test
    public void testBoolIntegerArray() throws Exception {
        assertEquals(false, bool((int[]) null));
        assertEquals(false, bool(new int[0]));
        assertEquals(true, bool(new int[1]));
    }

    @Test
    public void testBoolLongArray() throws Exception {
        assertEquals(false, bool((long[]) null));
        assertEquals(false, bool(new long[0]));
        assertEquals(true, bool(new long[1]));
    }

    @Test
    public void testBoolFloatArray() throws Exception {
        assertEquals(false, bool((float[]) null));
        assertEquals(false, bool(new float[0]));
        assertEquals(true, bool(new float[1]));
    }

    @Test
    public void testBoolDoubleArray() throws Exception {
        assertEquals(false, bool((double[]) null));
        assertEquals(false, bool(new double[0]));
        assertEquals(true, bool(new double[1]));
    }

    @Test
    public void testBoolFile() throws Exception {
        assertEquals(false, bool((File) null));
        assertEquals(false, bool(new File((File) null, "sdjfksdkjfsdf")));

        final File file;
        {
            file = new File(getClass().getClassLoader().getResource("scriptjava/buildins/BoolTest_file").toURI());
        }
        assertEquals(true, bool(file));
    }

    @Test
    public void testBoolObject() throws Exception {
        assertEquals(false, bool((Object) null));
        assertEquals(true, bool(new Object()));
    }
}