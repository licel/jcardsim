/*
 * Copyright 2011 Licel LLC.
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
package com.licel.jcardsim.crypto;

import javacard.security.Checksum;
import junit.framework.TestCase;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;

/**
 * Test for <code>CRC32</code>
 * Test data from NXP JCOP31-36 JavaCard
 */
public class CRC32Test extends TestCase {

    // etalon msg
    String MESSAGE = "C46A3D01F5494013F9DFF3C5392C64";
    // etalon crc
    String CRC = "7C6277D0";

    public CRC32Test(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of of class CRC16.
     */
    public void testCrc32() {
        System.out.println("test crc32");
        Checksum crcEngine = Checksum.getInstance(Checksum.ALG_ISO3309_CRC32, false);
        byte[] crc = new byte[4];
        byte[] msg = Hex.decode(MESSAGE);
        crcEngine.doFinal(msg, (short) 0, (short) msg.length, crc, (short) 0);
        assertEquals(true, Arrays.areEqual(Hex.decode(CRC), crc));
    }
}
