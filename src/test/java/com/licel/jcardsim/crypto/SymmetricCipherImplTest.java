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

import javacard.framework.Util;
import javacard.security.AESKey;
import javacard.security.Key;
import javacard.security.KeyBuilder;
import javacardx.crypto.Cipher;
import junit.framework.TestCase;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.encoders.Hex;

/**
 * Test for
 * <code>SymmetricCipherImpl</code> Test data from NXP JCOP31-36 JavaCard
 */
public class SymmetricCipherImplTest extends TestCase {

    // padded etalon message
    String MESSAGE_8 = "C899464893435BC8";
    // etalon message without padding
    String MESSAGE_15 = "C46A3D01F5494013F9DFF3C5392C64";
    // etalon des key
    String DES_KEY = "71705866C930F0AE";
    // etalon 3des key
    String DES3_KEY = "B1891A49B2EA69F21245D4A51DD132E24F247FAC6D97F007";
    // etalon IV vector
    String IV = "F8D7DB2B902855A3";
    // MESSAGE_15 encrypted by card (DES key)
    String[] DES_ENCRYPTED_15 = new String[]{
        // ALG_DES_CBC_ISO9797_M1
        "F38F388669A566CC2CC3B23F98A404FE",
        // ALG_DES_CBC_ISO9797_M2
        "F38F388669A566CCD7863C7D58BD53F4",
        // ALG_DES_ECB_ISO9797_M1
        "F38F388669A566CCE9DE32BDE856B809",
        // ALG_DES_ECB_ISO9797_M2
        "F38F388669A566CCC0A527F4726E318D",};
    // MESSAGE_15 encrypted by card (3DES key)
    String[] DES3_ENCRYPTED_15 = new String[]{
        // ALG_DES_CBC_ISO9797_M1
        "59AEEAFA9FD22B2E165DD117D24198B1",
        // ALG_DES_CBC_ISO9797_M2
        "59AEEAFA9FD22B2EC8D247D6209E2E44",
        // ALG_DES_ECB_ISO9797_M1
        "59AEEAFA9FD22B2E6948896A7E159FAF",
        // ALG_DES_ECB_ISO9797_M2
        "59AEEAFA9FD22B2EDAA807A92435BB13",};
    // MESSAGE_15 encrypted by card (DES key) in CBC mode with non-zero IV
    String[] DES_ENCRYPTED_15_IV = new String[]{
        // ALG_DES_CBC_ISO9797_M1
        "302A9CDD30BC0F9286D64C88EFE70383",
        // ALG_DES_CBC_ISO9797_M2
        "302A9CDD30BC0F921B66F319FA735F75",};
    // MESSAGE_15 encrypted by card (3DES key) in CBC mode with non-zero IV
    String[] DES3_ENCRYPTED_15_IV = new String[]{
        // ALG_DES_CBC_ISO9797_M1
        "70A88CEADAD491A0CC4EBC98BFFFAC21",
        // ALG_DES_CBC_ISO9797_M2
        "70A88CEADAD491A0EC17707C14FA1344",};
    // MESSAGE_8 encrypted by card (DES key)
    String[] DES_ENCRYPTED_8 = new String[]{
        // ALG_DES_CBC_NOPAD
        "8E5ABFB9D5F015EE",
        // ALG_DES_ECB_NOPAD
        "8E5ABFB9D5F015EE"
    };
    // MESSAGE_8 encrypted by card (3DES key)
    String[] DES3_ENCRYPTED_8 = new String[]{
        // ALG_DES_CBC_NOPAD
        "DB3543BCBB4EAD86",
        // ALG_DES_ECB_NOPAD
        "DB3543BCBB4EAD86"
    };
    // MESSAGE_8 encrypted by card (DES key) in CBC mode with non-zero IV
    String[] DES_ENCRYPTED_8_IV = new String[]{
        // ALG_DES_CBC_NOPAD
        "3CE9E2657AFCE8B6"
    };
    // MESSAGE_8 encrypted by card (3DES key) in CBC mode with non-zero IV
    String[] DES3_ENCRYPTED_8_IV = new String[]{
        // ALG_DES_CBC_NOPAD
        "81B2369E2773858F"
    };
    // AES test data from NIST (sp800-38a)
    // FORMAT: key:[iv]:data:result
    // Appendix F.1
    String[] AES_ECB_128_TEST = {"2b7e151628aed2a6abf7158809cf4f3c", "6bc1bee22e409f96e93d7e117393172a", "3ad77bb40d7a3660a89ecaf32466ef97"};
    // Appendix F.1.3
    String[] AES_ECB_192_TEST = {"8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "6bc1bee22e409f96e93d7e117393172a", "bd334f1d6e45f25ff712a214571fa5cc"};
    // Appendix F.1.5
    String[] AES_ECB_256_TEST = {"603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "6bc1bee22e409f96e93d7e117393172a", "f3eed1bdb5d2a03c064b5a7e3db181f8"};
    // Appendix F.2.1
    String[] AES_CBC_128_TEST = {"2b7e151628aed2a6abf7158809cf4f3c", "000102030405060708090a0b0c0d0e0f", "6bc1bee22e409f96e93d7e117393172a", "7649abac8119b246cee98e9b12e9197d"};
    // Appendix F.2.3
    String[] AES_CBC_192_TEST = {"8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b", "000102030405060708090a0b0c0d0e0f", "6bc1bee22e409f96e93d7e117393172a", "4f021db243bc633d7178183a9fa071e8"};
    // Appendix F.2.5
    String[] AES_CBC_256_TEST = {"603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4", "000102030405060708090a0b0c0d0e0f", "6bc1bee22e409f96e93d7e117393172a", "f58c4c04d6e5f1ba779eabfb5f7bfbd6"};

    public SymmetricCipherImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of doFinal method, of class SymmetricCipherImpl with 3DES Key
     */
    public void testDoFinal3DES() {
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_3KEY);
        desKey.setKey(Hex.decode(DES3_KEY), (short) 0);
        testDoFinalDES(desKey, MESSAGE_8, MESSAGE_15, DES3_ENCRYPTED_8,
                DES3_ENCRYPTED_15, DES3_ENCRYPTED_8_IV, DES3_ENCRYPTED_15_IV);
    }

    /**
     * Test of doFinal method, of class SymmetricCipherImpl with DES Key
     */
    public void testDoFinalDES() {
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES);
        desKey.setKey(Hex.decode(DES_KEY), (short) 0);
        testDoFinalDES(desKey, MESSAGE_8, MESSAGE_15, DES_ENCRYPTED_8,
                DES_ENCRYPTED_15, DES_ENCRYPTED_8_IV, DES_ENCRYPTED_15_IV);
    }

    /**
     * Test of doFinal method, of class SymmetricCipherImpl with specified key
     * and DES cipher
     */
    public void testDoFinalDES(SymmetricKeyImpl desKey, String msg8, String msg15,
            String[] enc8, String[] enc15, String[] enc8IV, String[] enc15IV) {
        // test DES CBC with IV={0,0,0,0,0,0,0,0}
        Cipher engine = Cipher.getInstance(Cipher.ALG_DES_CBC_NOPAD, false);
        testEngineDoFinal(engine, desKey, null, Hex.decode(msg8), Hex.decode(enc8[0]));

        engine = Cipher.getInstance(Cipher.ALG_DES_CBC_ISO9797_M1, false);
        testEngineDoFinal(engine, desKey, null, Hex.decode(msg15), Hex.decode(enc15[0]));

        engine = Cipher.getInstance(Cipher.ALG_DES_CBC_ISO9797_M2, false);
        testEngineDoFinal(engine, desKey, null, Hex.decode(msg15), Hex.decode(enc15[1]));

        // test DES CBC with non-zero IV
        byte[] iv = Hex.decode(IV);
        engine = Cipher.getInstance(Cipher.ALG_DES_CBC_NOPAD, false);
        testEngineDoFinal(engine, desKey, iv, Hex.decode(msg8), Hex.decode(enc8IV[0]));

        engine = Cipher.getInstance(Cipher.ALG_DES_CBC_ISO9797_M1, false);
        testEngineDoFinal(engine, desKey, iv, Hex.decode(msg15), Hex.decode(enc15IV[0]));

        engine = Cipher.getInstance(Cipher.ALG_DES_CBC_ISO9797_M2, false);
        testEngineDoFinal(engine, desKey, iv, Hex.decode(msg15), Hex.decode(enc15IV[1]));

        // test DES ECB
        engine = Cipher.getInstance(Cipher.ALG_DES_ECB_NOPAD, false);
        testEngineDoFinal(engine, desKey, null, Hex.decode(msg8), Hex.decode(enc8[1]));

        engine = Cipher.getInstance(Cipher.ALG_DES_ECB_ISO9797_M1, false);
        testEngineDoFinal(engine, desKey, null, Hex.decode(msg15), Hex.decode(enc15[2]));

        engine = Cipher.getInstance(Cipher.ALG_DES_ECB_ISO9797_M2, false);
        testEngineDoFinal(engine, desKey, null, Hex.decode(msg15), Hex.decode(enc15[3]));

    }

    public void testAes() {
        testAESMode(KeyBuilder.LENGTH_AES_128, Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, AES_ECB_128_TEST);
        testAESMode(KeyBuilder.LENGTH_AES_192, Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, AES_ECB_192_TEST);
        testAESMode(KeyBuilder.LENGTH_AES_256, Cipher.ALG_AES_BLOCK_128_ECB_NOPAD, AES_ECB_256_TEST);
        testAESMode(KeyBuilder.LENGTH_AES_128, Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, AES_CBC_128_TEST);
        testAESMode(KeyBuilder.LENGTH_AES_192, Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, AES_CBC_192_TEST);
        testAESMode(KeyBuilder.LENGTH_AES_256, Cipher.ALG_AES_BLOCK_128_CBC_NOPAD, AES_CBC_256_TEST);
    }

    /**
     * Test AES cipher mode
     */
    public void testAESMode(short keyLen, byte mode, String[] testData) {
        short keyLenInBytes = (short) (keyLen / 8);
        Cipher engine = Cipher.getInstance(mode, false);
        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, keyLen, false);
        byte[] etalonKey = Hex.decode(testData[0]);
        byte[] key = new byte[keyLenInBytes];
        Util.arrayCopy(etalonKey, (short) 0, key, (short) 0, (short) etalonKey.length);
        aesKey.setKey(key, (short) 0);
        boolean needIV = (mode == Cipher.ALG_AES_BLOCK_128_CBC_NOPAD);
        if (needIV) {
            byte[] iv = Hex.decode(testData[1]);
            engine.init(aesKey, Cipher.MODE_ENCRYPT, iv, (short)0, (short)iv.length);
        } else {
            engine.init(aesKey, Cipher.MODE_ENCRYPT);
        }
        byte[] encrypted = new byte[16]; // AES 128
        short processedBytes = engine.doFinal(Hex.decode(testData[needIV?2:1]), (short) 0, (short) 16, encrypted, (short) 0);
        assertEquals(processedBytes, 16);
        assertEquals(true, Arrays.areEqual(encrypted, Hex.decode(testData[needIV?3:2])));
        if (needIV) {
            byte[] iv = Hex.decode(testData[1]);
            engine.init(aesKey, Cipher.MODE_DECRYPT, iv, (short)0, (short)iv.length);
        } else {
            engine.init(aesKey, Cipher.MODE_DECRYPT);
        }
        byte[] decrypted = new byte[16]; // AES 128
        processedBytes = engine.doFinal(Hex.decode(testData[needIV?3:2]), (short) 0, (short) 16, decrypted, (short) 0);
        assertEquals(processedBytes, 16);
        assertEquals(true, Arrays.areEqual(decrypted, Hex.decode(testData[needIV?2:1])));
    }

    /**
     * Test method
     * <code>doFinal</code>
     *
     * @param engine test engine
     * @param key etalon key
     * @param iv IV if present
     * @param msg etalon message
     * @param encryptedEtalonMsg encrypted etalon message
     */
    public void testEngineDoFinal(Cipher engine, Key key, byte[] iv, byte[] msg, byte[] encryptedEtalonMsg) {
        // first test equals encryption
        if (iv == null) {
            engine.init(key, Cipher.MODE_ENCRYPT);
        } else {
            engine.init(key, Cipher.MODE_ENCRYPT, iv, (short) 0, (short) iv.length);
        }
        byte[] encrypted = new byte[encryptedEtalonMsg.length];
        short processedBytes = engine.doFinal(msg, (short) 0, (short) msg.length, encrypted, (short) 0);
        assertEquals(true, Arrays.areEqual(encrypted, encryptedEtalonMsg));
        assertEquals(processedBytes, encryptedEtalonMsg.length);
        // second test decryption
        if (iv == null) {
            engine.init(key, Cipher.MODE_DECRYPT);
        } else {
            engine.init(key, Cipher.MODE_DECRYPT, iv, (short) 0, (short) iv.length);
        }
        byte[] decrypted = new byte[msg.length];
        processedBytes = engine.doFinal(encryptedEtalonMsg, (short) 0, (short) encryptedEtalonMsg.length, decrypted, (short) 0);
        assertEquals(processedBytes, msg.length);
        assertEquals(true, Arrays.areEqual(decrypted, msg));
    }
}
