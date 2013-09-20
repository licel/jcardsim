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

import javacard.framework.JCSystem;
import javacard.security.Key;
import javacard.security.KeyBuilder;
import javacard.security.Signature;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/**
 * Test for <code>SymmetricSignatureImpl</code>
 * Test data from NXP JCOP31-36 JavaCard
 */
public class SymmetricSignatureImplTest extends TestCase {

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
    // MESSAGE_15 MAC by card (DES key)
    String[] DES_MAC_15 = new String[]{
        // ALG_DES_MAC8_ISO9797_M1
        "2CC3B23F98A404FE",
        // ALG_DES_MAC8_ISO9797_M2
        "D7863C7D58BD53F4"
    };
    // MESSAGE_15 MAC by card (3DES key)
    String[] DES3_MAC_15 = new String[]{
        // ALG_DES_MAC8_ISO9797_M1
        "165DD117D24198B1",
        // ALG_DES_MAC8_ISO9797_M2
        "C8D247D6209E2E44",};
    // MESSAGE_15 MAC by card (DES key) with non-zero IV
    String[] DES_MAC_15_IV = new String[]{
        // ALG_DES_MAC8_ISO9797_M1
        "86D64C88EFE70383",
        // ALG_DES_MAC8_ISO9797_M1
        "1B66F319FA735F75",};
    // MESSAGE_15 MAC by card (3DES key) with non-zero IV
    String[] DES3_MAC_15_IV = new String[]{
        // ALG_DES_MAC8_ISO9797_M1
        "CC4EBC98BFFFAC21",
        // ALG_DES_MAC8_ISO9797_M2
        "EC17707C14FA1344",};
    // MESSAGE_8 MAC by card (DES key)
    String[] DES_MAC_8 = new String[]{
        // ALG_DES_MAC8_NOPAD
        "8E5ABFB9D5F015EE",};
    // MESSAGE_8 MAC by card (3DES key)
    String[] DES3_MAC_8 = new String[]{
        // ALG_DES_MAC8_NOPAD
        "DB3543BCBB4EAD86",};
    // MESSAGE_8 MAC by card (DES key)  with non-zero IV
    String[] DES_MAC_8_IV = new String[]{
        // ALG_DES_MAC8_NOPAD
        "3CE9E2657AFCE8B6"
    };
    // MESSAGE_8 MAC by card (3DES key)  with non-zero IV
    String[] DES3_MAC_8_IV = new String[]{
        // ALG_DES_MAC8_NOPAD
        "81B2369E2773858F"
    };

    public SymmetricSignatureImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of sign/verify methods, of class SymmetricSignatureImpl with 3DES Key
     */
    public void testSignVerify3DES() {
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_3KEY);
        desKey.setKey(Hex.decode(DES3_KEY), (short) 0);
        testSignVerify(desKey, MESSAGE_8, MESSAGE_15, DES3_MAC_8,
                DES3_MAC_15, DES3_MAC_8_IV, DES3_MAC_15_IV);
    }

    /**
     * Test of sign/verifys methods, of class SymmetricSignatureImpl with DES Key
     */
    public void testSignVerifyDES() {
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES);
        desKey.setKey(Hex.decode(DES_KEY), (short) 0);
        testSignVerify(desKey, MESSAGE_8, MESSAGE_15, DES_MAC_8,
                DES_MAC_15, DES_MAC_8_IV, DES_MAC_15_IV);
    }

    /**
     * Test of sign/verify methods, of class SymmetricSignatureImpl with specified key
     * and etalon data
     */
    public void testSignVerify(SymmetricKeyImpl desKey, String msg8, String msg15,
            String[] enc8, String[] enc15, String[] enc8IV, String[] enc15IV) {

        // test DES MAC CBC with IV={0,0,0,0,0,0,0,0}
        Signature engine = Signature.getInstance(Signature.ALG_DES_MAC8_NOPAD, false);
        testEngineSignVerify(engine, desKey, null, Hex.decode(msg8), Hex.decode(enc8[0]));

        engine = Signature.getInstance(Signature.ALG_DES_MAC8_ISO9797_M1, false);
        testEngineSignVerify(engine, desKey, null, Hex.decode(msg15), Hex.decode(enc15[0]));

        engine = Signature.getInstance(Signature.ALG_DES_MAC8_ISO9797_M2, false);
        testEngineSignVerify(engine, desKey, null, Hex.decode(msg15), Hex.decode(enc15[1]));

        // test DES MAC CBC with non-zero IV
        byte[] iv = Hex.decode(IV);
        engine = Signature.getInstance(Signature.ALG_DES_MAC8_NOPAD, false);
        testEngineSignVerify(engine, desKey, iv, Hex.decode(msg8), Hex.decode(enc8IV[0]));

        engine = Signature.getInstance(Signature.ALG_DES_MAC8_ISO9797_M1, false);
        testEngineSignVerify(engine, desKey, iv, Hex.decode(msg15), Hex.decode(enc15IV[0]));

        engine = Signature.getInstance(Signature.ALG_DES_MAC8_ISO9797_M2, false);
        testEngineSignVerify(engine, desKey, iv, Hex.decode(msg15), Hex.decode(enc15IV[1]));
    }

    /**
     * Test of sign/verify methods, of class SymmetricSignatureImpl with specified key, engine
     * and etalon data
     * @param engine test engine
     * @param key etalon key
     * @param iv IV if present
     * @param msg etalon msg
     * @param macEtalon etalon signature(mac)
     */
    public void testEngineSignVerify(Signature engine, Key key, byte[] iv, byte[] msg, byte[] macEtalon) {
        // sign
        if (iv == null) {
            engine.init(key, Signature.MODE_SIGN);
        } else {
            engine.init(key, Signature.MODE_SIGN, iv, (short) 0, (short) iv.length);
        }
        byte[] mac = JCSystem.makeTransientByteArray((short) macEtalon.length, JCSystem.CLEAR_ON_RESET);
        //
        engine.sign(msg, (short) 0, (short) msg.length, mac, (short) 0);
        assertEquals(true, Arrays.areEqual(mac, macEtalon));
        // verify
        if (iv == null) {
            engine.init(key, Signature.MODE_VERIFY);
        } else {
            engine.init(key, Signature.MODE_VERIFY, iv, (short) 0, (short) iv.length);
        }
        assertEquals(true, engine.verify(msg, (short) 0, (short) msg.length, macEtalon,
                (short) 0, (short) macEtalon.length));

    }
}
