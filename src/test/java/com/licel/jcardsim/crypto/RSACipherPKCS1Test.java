/*
 * Copyright 2013 Licel LLC.
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
import javacard.security.RandomData;
import javacard.security.RSAPrivateKey;
import javacard.security.RSAPublicKey;
import javacardx.crypto.Cipher;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;

/**
 * Test for <code>AsymmetricCipherImpl</code> and <code>ALG_RSA_PKCS1</code> algorithm implementation.
 */
public class RSACipherPKCS1Test extends TestCase {

    // RSA keypair data
    private static final byte[] rsaPrivateKeyModulus = {
        (byte) 0xbe, (byte) 0xdf, (byte) 0xd3, (byte) 0x7a, (byte) 0x08,
        (byte) 0xe2, (byte) 0x9a, (byte) 0x58, (byte) 0x27, (byte) 0x54,
        (byte) 0x2a, (byte) 0x49, (byte) 0x18, (byte) 0xce, (byte) 0xe4,
        (byte) 0x1a, (byte) 0x60, (byte) 0xdc, (byte) 0x62, (byte) 0x75,
        (byte) 0xbd, (byte) 0xb0, (byte) 0x8d, (byte) 0x15, (byte) 0xa3,
        (byte) 0x65, (byte) 0xe6, (byte) 0x7b, (byte) 0xa9, (byte) 0xdc,
        (byte) 0x09, (byte) 0x11, (byte) 0x5f, (byte) 0x9f, (byte) 0xbf,
        (byte) 0x29, (byte) 0xe6, (byte) 0xc2, (byte) 0x82, (byte) 0xc8,
        (byte) 0x35, (byte) 0x6b, (byte) 0x0f, (byte) 0x10, (byte) 0x9b,
        (byte) 0x19, (byte) 0x62, (byte) 0xfd, (byte) 0xbd, (byte) 0x96,
        (byte) 0x49, (byte) 0x21, (byte) 0xe4, (byte) 0x22, (byte) 0x08,
        (byte) 0x08, (byte) 0x80, (byte) 0x6c, (byte) 0xd1, (byte) 0xde,
        (byte) 0xa6, (byte) 0xd3, (byte) 0xc3, (byte) 0x8f };

    private static final byte[] rsaPrivateKeyExponent = {
        (byte) 0x84, (byte) 0x21, (byte) 0xfe, (byte) 0x0b, (byte) 0xa4,
        (byte) 0xca, (byte) 0xf9, (byte) 0x7d, (byte) 0xbc, (byte) 0xfc,
        (byte) 0x0e, (byte) 0xa9, (byte) 0xbb, (byte) 0x7a, (byte) 0xbd,
        (byte) 0x7d, (byte) 0x65, (byte) 0x40, (byte) 0x2b, (byte) 0x08,
        (byte) 0xc6, (byte) 0xdf, (byte) 0xc9, (byte) 0x4b, (byte) 0x09,
        (byte) 0x6a, (byte) 0x29, (byte) 0x3b, (byte) 0xc2, (byte) 0x42,
        (byte) 0x88, (byte) 0x23, (byte) 0x44, (byte) 0xaf, (byte) 0x08,
        (byte) 0x82, (byte) 0x4c, (byte) 0xff, (byte) 0x42, (byte) 0xa4,
        (byte) 0xb8, (byte) 0xd2, (byte) 0xda, (byte) 0xcc, (byte) 0xee,
        (byte) 0xc5, (byte) 0x34, (byte) 0xed, (byte) 0x71, (byte) 0x01,
        (byte) 0xab, (byte) 0x3b, (byte) 0x76, (byte) 0xde, (byte) 0x6c,
        (byte) 0xa2, (byte) 0xcb, (byte) 0x7c, (byte) 0x38, (byte) 0xb6,
        (byte) 0x9a, (byte) 0x4b, (byte) 0x28, (byte) 0x01 };

    private static final byte[] rsaPublicKeyExponent = {
        (byte) 0x01, (byte) 0x00, (byte) 0x01 };

    public RSACipherPKCS1Test(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * SelfTest of RSA Encryption/Decryption, of class AssymetricCipherImpl and ALG_RSA_PKCS1 algorithm implementation.
     */
    public void testRSAPKCS1() {
        Cipher cipher = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);

        RSAPrivateKey privateKey = (RSAPrivateKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PRIVATE, KeyBuilder.LENGTH_RSA_512, false);
        RSAPublicKey publicKey = (RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC, KeyBuilder.LENGTH_RSA_512, false);

        privateKey.setExponent(rsaPrivateKeyExponent, (short) 0, (short) rsaPrivateKeyExponent.length);
        privateKey.setModulus(rsaPrivateKeyModulus, (short) 0,(short) rsaPrivateKeyModulus.length);
        publicKey.setExponent(rsaPublicKeyExponent, (short) 0,(short) rsaPublicKeyExponent.length);
        publicKey.setModulus(rsaPrivateKeyModulus, (short) 0,(short) rsaPrivateKeyModulus.length);

        cipher.init(publicKey, Cipher.MODE_ENCRYPT);
        byte[] msg = JCSystem.makeTransientByteArray((short) 53, JCSystem.CLEAR_ON_RESET);
        byte[] encryptedMsg = JCSystem.makeTransientByteArray((short) 64, JCSystem.CLEAR_ON_RESET);
        RandomData rnd = RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);
        rnd.generateData(msg, (short) 0, (short) msg.length);
        cipher.doFinal(msg, (short) 0, (short) msg.length, encryptedMsg, (short) 0);

        cipher.init(privateKey, Cipher.MODE_DECRYPT);
        byte[] decryptedMsg = JCSystem.makeTransientByteArray((short) msg.length, JCSystem.CLEAR_ON_RESET);
        cipher.doFinal(encryptedMsg, (short) 0, (short) encryptedMsg.length, decryptedMsg, (short) 0);

        assertEquals(true, Arrays.areEqual(msg, decryptedMsg));
    }
}
