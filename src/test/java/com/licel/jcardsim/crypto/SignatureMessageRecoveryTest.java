/*
 * Copyright 2014 Licel LLC.
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
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.RSAPrivateKey;
import javacard.security.RSAPublicKey;
import javacard.security.Signature;
import javacard.security.SignatureMessageRecovery;
import junit.framework.TestCase;

/**
 * SignatureMessageRecovery Test
 * based on JCDK Sample
 */
public class SignatureMessageRecoveryTest extends TestCase {

    //--RSA Keypair data
    private static final byte[] RSA_PUB_KEY_EXP = {(byte) 0x01, (byte) 0x00, (byte) 0x01};
    private static final byte[] RSA_PUB_PRIV_KEY_MOD = {(byte) 0xbe, (byte) 0xdf,
        (byte) 0xd3, (byte) 0x7a, (byte) 0x08, (byte) 0xe2, (byte) 0x9a, (byte) 0x58,
        (byte) 0x27, (byte) 0x54, (byte) 0x2a, (byte) 0x49, (byte) 0x18, (byte) 0xce,
        (byte) 0xe4, (byte) 0x1a, (byte) 0x60, (byte) 0xdc, (byte) 0x62, (byte) 0x75,
        (byte) 0xbd, (byte) 0xb0, (byte) 0x8d, (byte) 0x15, (byte) 0xa3, (byte) 0x65,
        (byte) 0xe6, (byte) 0x7b, (byte) 0xa9, (byte) 0xdc, (byte) 0x09, (byte) 0x11,
        (byte) 0x5f, (byte) 0x9f, (byte) 0xbf, (byte) 0x29, (byte) 0xe6, (byte) 0xc2,
        (byte) 0x82, (byte) 0xc8, (byte) 0x35, (byte) 0x6b, (byte) 0x0f, (byte) 0x10,
        (byte) 0x9b, (byte) 0x19, (byte) 0x62, (byte) 0xfd, (byte) 0xbd, (byte) 0x96,
        (byte) 0x49, (byte) 0x21, (byte) 0xe4, (byte) 0x22, (byte) 0x08, (byte) 0x08,
        (byte) 0x80, (byte) 0x6c, (byte) 0xd1, (byte) 0xde, (byte) 0xa6, (byte) 0xd3,
        (byte) 0xc3, (byte) 0x8f};
    private static final byte[] RSA_PRIV_KEY_EXP = {(byte) 0x84, (byte) 0x21,
        (byte) 0xfe, (byte) 0x0b, (byte) 0xa4, (byte) 0xca, (byte) 0xf9, (byte) 0x7d,
        (byte) 0xbc, (byte) 0xfc, (byte) 0x0e, (byte) 0xa9, (byte) 0xbb, (byte) 0x7a,
        (byte) 0xbd, (byte) 0x7d, (byte) 0x65, (byte) 0x40, (byte) 0x2b, (byte) 0x08,
        (byte) 0xc6, (byte) 0xdf, (byte) 0xc9, (byte) 0x4b, (byte) 0x09, (byte) 0x6a,
        (byte) 0x29, (byte) 0x3b, (byte) 0xc2, (byte) 0x42, (byte) 0x88, (byte) 0x23,
        (byte) 0x44, (byte) 0xaf, (byte) 0x08, (byte) 0x82, (byte) 0x4c, (byte) 0xff,
        (byte) 0x42, (byte) 0xa4, (byte) 0xb8, (byte) 0xd2, (byte) 0xda, (byte) 0xcc,
        (byte) 0xee, (byte) 0xc5, (byte) 0x34, (byte) 0xed, (byte) 0x71, (byte) 0x01,
        (byte) 0xab, (byte) 0x3b, (byte) 0x76, (byte) 0xde, (byte) 0x6c, (byte) 0xa2,
        (byte) 0xcb, (byte) 0x7c, (byte) 0x38, (byte) 0xb6, (byte) 0x9a, (byte) 0x4b,
        (byte) 0x28, (byte) 0x01};
    RSAPublicKey pubKey;
    RSAPrivateKey privKey;
    SignatureMessageRecovery sig;
    KeyPair selfTestKeys;

    /**
     * Only this class's install method should create the applet object.
     */
    protected void setUp() throws Exception {
        super.setUp();
        pubKey = (RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_512, false);
        privKey = (RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_512, false);
        privKey.setExponent(RSA_PRIV_KEY_EXP, (short) 0, (short) RSA_PRIV_KEY_EXP.length);
        privKey.setModulus(RSA_PUB_PRIV_KEY_MOD, (short) 0, (short) RSA_PUB_PRIV_KEY_MOD.length);
        pubKey.setExponent(RSA_PUB_KEY_EXP, (short) 0, (short) RSA_PUB_KEY_EXP.length);
        pubKey.setModulus(RSA_PUB_PRIV_KEY_MOD, (short) 0, (short) RSA_PUB_PRIV_KEY_MOD.length);
        sig = (SignatureMessageRecovery) Signature.getInstance(Signature.ALG_RSA_SHA_ISO9796_MR, false);
        selfTestKeys = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_2048);
        selfTestKeys.genKeyPair();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     *
     */
    public void testCryptoSignAndVerifyFullMsgRecovery() {
        byte[] buffer = new byte[1];

        sig.init(pubKey, Signature.MODE_VERIFY);
        byte[] etalonSign = {(byte) 0xa3, (byte) 0x49, (byte) 0x1d, (byte) 0x51, (byte) 0x55,
            (byte) 0x05, (byte) 0x49, (byte) 0x71, (byte) 0xba, (byte) 0xdc, (byte) 0x77, (byte) 0x22,
            (byte) 0xce, (byte) 0x9a, (byte) 0x51, (byte) 0x71, (byte) 0xf8, (byte) 0xb1, (byte) 0x88,
            (byte) 0x8d, (byte) 0x55, (byte) 0x05, (byte) 0xd5, (byte) 0x2b, (byte) 0xae, (byte) 0xf6,
            (byte) 0xb7, (byte) 0x04, (byte) 0xd9, (byte) 0x1d, (byte) 0x09, (byte) 0x35, (byte) 0x17,
            (byte) 0xec, (byte) 0x73, (byte) 0x11, (byte) 0xd5, (byte) 0x7f, (byte) 0xfd, (byte) 0xeb,
            (byte) 0xb3, (byte) 0xd9, (byte) 0x98, (byte) 0x45, (byte) 0xf7, (byte) 0x8a, (byte) 0xb6,
            (byte) 0x72, (byte) 0x21, (byte) 0x44, (byte) 0xa1, (byte) 0x32, (byte) 0xb3, (byte) 0xa1,
            (byte) 0xce, (byte) 0x72, (byte) 0xc5, (byte) 0x6d, (byte) 0xcc, (byte) 0xee, (byte) 0x18,
            (byte) 0x64, (byte) 0x2e, (byte) 0x76};

        short m1Length = sig.beginVerify(etalonSign, (short) 0, (short) etalonSign.length);
        boolean verified = sig.verify(buffer, (short) 0, (short) 0);

        assertEquals(m1Length, 1);

        assertEquals(true, verified);

    }

    public void testSelfCryptoSignAndVerifyFullMsgRecovery() {
        byte[] data = new byte[41];
        for (byte i = 0; i < data.length; i++) {
            data[i] = i;
        }
        short[] m1Data = JCSystem.makeTransientShortArray((short) 1, JCSystem.CLEAR_ON_DESELECT);
        byte[] signature = new byte[(short)256];

        sig.init(selfTestKeys.getPrivate(), Signature.MODE_SIGN);
        short sigLen = sig.sign(data, (short) 0, (short) data.length, signature, (short) 0, m1Data, (short) 0);

        sig.init(selfTestKeys.getPublic(), Signature.MODE_VERIFY);
        short m1Length = sig.beginVerify(signature, (short) 0, sigLen);

        boolean verified = sig.verify(data, (short) 0, (short) 0);

        assertEquals(m1Length, m1Data[0]);

        assertEquals(true, verified);

    }

    public void testCryptoVerifyPartMsgRecovery() {
        byte[] data = new byte[70];
        for (byte i = 0; i < data.length; i++) {
            data[i] = i;
        }

        byte[] etalonSign = new byte[]{(byte) 0x2d, (byte) 0x15, (byte) 0x79, (byte) 0x89,
            (byte) 0xba, (byte) 0x71, (byte) 0x6d, (byte) 0x31, (byte) 0x6c, (byte) 0x0e, (byte) 0x29,
            (byte) 0x55, (byte) 0xc0, (byte) 0x0e, (byte) 0x80, (byte) 0xc3, (byte) 0x5c, (byte) 0xa3,
            (byte) 0xe8, (byte) 0xa1, (byte) 0x12, (byte) 0x65, (byte) 0xe3, (byte) 0x6f, (byte) 0xb2,
            (byte) 0x51, (byte) 0x44, (byte) 0x7d, (byte) 0x30, (byte) 0x4a, (byte) 0x24, (byte) 0xcf,
            (byte) 0xa1, (byte) 0x1b, (byte) 0xaa, (byte) 0x30, (byte) 0x48, (byte) 0xd3, (byte) 0x70,
            (byte) 0x4a, (byte) 0x0b, (byte) 0xe7, (byte) 0x9a, (byte) 0x05, (byte) 0x1f, (byte) 0x5f,
            (byte) 0x87, (byte) 0xc7, (byte) 0x8f, (byte) 0xe4, (byte) 0xae, (byte) 0xbc, (byte) 0xde,
            (byte) 0x0a, (byte) 0x63, (byte) 0x6a, (byte) 0x28, (byte) 0x48, (byte) 0x52, (byte) 0xc0,
            (byte) 0xe7, (byte) 0xd2, (byte) 0x7f, (byte) 0xfe};

        //recover the recoverable message from signature
        sig.init(pubKey, Signature.MODE_VERIFY);
        short m1Length = sig.beginVerify(etalonSign, (short) 0, (short) etalonSign.length);

        assertEquals(m1Length, 42);


        byte[] etalonNonRecMsg = new byte[]{(byte) 0x2b, (byte) 0x2c, (byte) 0x2d, (byte) 0x2e,
            (byte) 0x2f, (byte) 0x30, (byte) 0x31, (byte) 0x32, (byte) 0x33, (byte) 0x34, (byte) 0x35, (byte) 0x36,
            (byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x3a, (byte) 0x3b, (byte) 0x3c, (byte) 0x3d, (byte) 0x3e,
            (byte) 0x3f, (byte) 0x40, (byte) 0x41, (byte) 0x42, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46};

        boolean verified = sig.verify(etalonNonRecMsg, (short) 0, (short) etalonNonRecMsg.length);
        assertEquals(true, verified);

    }

    public void testSelfCryptoSignAndVerifyPartMsgRecovery() {
        byte[] data = new byte[(short)256];
        for (short i = 0; i < data.length; i++) {
            data[i] = (byte)i;
        }
        short[] m1Data = JCSystem.makeTransientShortArray((short) 1, JCSystem.CLEAR_ON_DESELECT);
        byte[] signature = new byte[(short)256];

        sig.init(selfTestKeys.getPrivate(), Signature.MODE_SIGN);
        short sigLen = sig.sign(data, (short) 0, (short) data.length, signature, (short) 0, m1Data, (short) 0);

        sig.init(selfTestKeys.getPublic(), Signature.MODE_VERIFY);
        short m1Length = sig.beginVerify(signature, (short) 0, sigLen);

        boolean verified = sig.verify(data, m1Length, (short) (data.length - m1Length));

        assertEquals(m1Length, m1Data[0]);

        assertEquals(true, verified);

    }
}
