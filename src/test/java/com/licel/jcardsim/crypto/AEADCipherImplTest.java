/*
 * Copyright 2022 Karsten Ohme
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

import javacard.framework.Util;
import javacard.security.AESKey;
import javacard.security.KeyBuilder;
import javacardx.crypto.AEADCipher;
import javacardx.crypto.Cipher;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/**
 * Test for
 * <code>AEADCipherImpl</code> Test data from <a href="https://datatracker.ietf.org/doc/html/rfc3610">RFC 3610</a>
 */
public class AEADCipherImplTest extends TestCase {

    // FORMAT: key:nonce:associated:data:result:mac
    String[] VECTOR1 = {"C0C1C2C3C4C5C6C7C8C9CACBCCCDCECF", "00000003020100A0A1A2A3A4A5",
            "0001020304050607",
            "08090A0B0C0D0E0F101112131415161718191A1B1C1D1E",
            "0001020304050607588C979A61C663D2F066D0C2C0F989806D5F6B61DAC38417E8D12CFDF926E0",
            "2DC697E411CA83A8"};

    String[] VECTOR2 = {"C0C1C2C3C4C5C6C7C8C9CACBCCCDCECF", "00000004030201A0A1A2A3A4A5",
            "0001020304050607",
            "08090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F",
            "000102030405060772C91A36E135F8CF291CA894085C87E3CC15C439C9E43A3BA091D56E10400916",
            "F7B9056A86926CF3"};

    public AEADCipherImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testCcm() {
        test(KeyBuilder.LENGTH_AES_128, AEADCipher.ALG_AES_CCM, VECTOR1);
        test(KeyBuilder.LENGTH_AES_128, AEADCipher.ALG_AES_CCM, VECTOR2);
    }

    public void test(short keyLen, byte mode, String[] testData) {
        short keyLenInBytes = (short) (keyLen / 8);
        AEADCipher engine = (AEADCipher) AEADCipher.getInstance(mode, false);
        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, keyLen, false);
        byte[] testKey = Hex.decode(testData[0]);
        byte[] key = new byte[keyLenInBytes];
        Util.arrayCopy(testKey, (short) 0, key, (short) 0, (short) testKey.length);
        aesKey.setKey(key, (short) 0);
        byte[] nonce = Hex.decode(testData[1]);
        byte[] adata = Hex.decode(testData[2]);
        byte[] data = Hex.decode(testData[3]);
        byte[] result = Hex.decode(testData[4]);
        byte[] mac = Hex.decode(testData[5]);

        short tagSize = (short) (result.length - data.length - adata.length);

        // encrypt
        engine.init(aesKey, Cipher.MODE_ENCRYPT, nonce, (short) 0, (short) nonce.length, (short) adata.length,
                (short) data.length, tagSize);

        byte[] encrypted = new byte[result.length - adata.length];
        engine.updateAAD(adata, (short) 0, (short) adata.length);
        short processedBytes = engine.doFinal(data, (short) 0, (short) data.length, encrypted, (short) 0);

        assertEquals(processedBytes, result.length - adata.length);
        assertTrue(Arrays.areEqual(encrypted, 0, encrypted.length, result, adata.length, result.length));
        byte[] tag = new byte[tagSize];
        engine.retrieveTag(tag, (short) 0, tagSize);
        assertTrue(Arrays.areEqual(tag, mac));

        // decrypt
        engine.init(aesKey, Cipher.MODE_DECRYPT, nonce, (short) 0, (short) nonce.length, (short) adata.length,
                (short) data.length, tagSize);
        byte[] decrypted = new byte[data.length];
        engine.updateAAD(adata, (short) 0, (short) adata.length);
        processedBytes = engine.doFinal(result, (short) adata.length, (short) (result.length - adata.length), decrypted, (short) 0);
        assertEquals(processedBytes, data.length);
        assertTrue(Arrays.areEqual(decrypted, data));
        assertTrue(engine.verifyTag(tag, (short) 0, tagSize, tagSize));
    }

}
