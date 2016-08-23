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

public class Range {

    public static int[] range(int end) {
        return createRange(0, end, 1);
    }

    public static int[] range(int start, int end) {
        return createRange(start, end, 1);
    }

    public static int[] range(int start, int end, int step) {
        return createRange(start, end, step);
    }

    private static int[] createRange(int start, int end, int step) {

        // let's check if we can create a range, for example for start=0, end=1 we won't be iterating
        if (step == 0
                || (step > 0 && ((end - start) < step))
                || (step < 0 && ((start + end) > step))) {
            return new int[0];
        }

        final int length;
        {
            final int intSteps = (end - start) / step;
            if (intSteps == 0) {
                length = 0;
            } else {
                final float floatSteps = ((float) end - start) / step;
                if (Float.compare(floatSteps, intSteps) == 0) {
                    length = intSteps;
                } else {
                    length = intSteps + 1;
                }
            }
        }

        if (length == 0) {
            return new int[0];
        }

        final int[] out = new int[length]; // count the elements size
        for (int i = 0; i < length; i++) {
            out[i] = start + (step * i);
        }

        return out;
    }

    private Range() {

    }
}
