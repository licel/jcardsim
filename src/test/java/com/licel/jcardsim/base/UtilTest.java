/*
 * Copyright 2026 Licel Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.licel.jcardsim.base;

import java.util.Arrays;
import javacard.framework.Util;
import junit.framework.TestCase;

/**
 * Test for <code>Util</code>
 */
public class UtilTest extends TestCase {

    public UtilTest(String name) {
        super(name);
    }

    public void testArrayFill() {
        byte[] buf = new byte[]{1, 2, 3, 4, 5, 6};
        short ret = Util.arrayFill(buf, (short) 1, (short) 3, (byte) 0x5A);
        assertEquals((short) 4, ret);
        assertTrue(Arrays.equals(new byte[]{1, 0x5A, 0x5A, 0x5A, 5, 6}, buf));
    }

    public void testArrayFillZeroLength() {
        byte[] buf = new byte[]{1, 2, 3, 4};
        short ret = Util.arrayFill(buf, (short) 4, (short) 0, (byte) 0x7F);
        assertEquals((short) 4, ret);
        assertTrue(Arrays.equals(new byte[]{1, 2, 3, 4}, buf));
    }

    public void testArrayFillNullThrowsNPE() {
        try {
            Util.arrayFill(null, (short) 0, (short) 1, (byte) 0);
            fail("Expected NullPointerException");
        } catch (NullPointerException expected) {
        }
    }

    public void testArrayFillOutOfBoundsDoesNotModifyArray() {
        byte[] buf = new byte[]{1, 2, 3, 4};
        short[][] invalidArgs = new short[][]{
            {-1, 1},
            {0, -1},
            {2, 3},
            {5, 0},
        };
        for (int i = 0; i < invalidArgs.length; i++) {
            try {
                Util.arrayFill(buf, invalidArgs[i][0], invalidArgs[i][1], (byte) 0xFF);
                fail("Expected ArrayIndexOutOfBoundsException for case " + i);
            } catch (ArrayIndexOutOfBoundsException expected) {
                assertTrue(Arrays.equals(new byte[]{1, 2, 3, 4}, buf));
            }
        }
    }
}
