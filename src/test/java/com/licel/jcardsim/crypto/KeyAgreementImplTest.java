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

import javacard.security.ECPublicKey;
import javacard.security.KeyAgreement;
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.PrivateKey;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;

/**
 * Test for <code>KeyAgreementImpl</code>
 * Test data from NXP JCOP31-36 JavaCard
 */
public class KeyAgreementImplTest extends TestCase {

    public KeyAgreementImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * SelfTest of generateSecret method with ECDH algorithm, 
     * of class KeyAgreementImpl.
     */
    public void testGenerateSecretECDH() {
        System.out.println("test ecdh");
        testGenerateSecret(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, KeyAgreement.ALG_EC_SVDP_DH);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_SVDP_DH);
        System.out.println("test ecdhc");
        testGenerateSecret(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, KeyAgreement.ALG_EC_SVDP_DHC);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_SVDP_DHC);
    }

    /**
     * Base method generateSecret
     * @param keyAlg - key generation algorithm
     * @param keySize - key size
     * @param keyAgreementAlg - key agreement algorithm
     */
    public void testGenerateSecret(byte keyAlg, short keySize, byte keyAgreementAlg) {
        // generate keys
        KeyPair kp = new KeyPair(keyAlg, keySize);
        kp.genKeyPair();
        PrivateKey privateKey1 = kp.getPrivate();
        ECPublicKey publicKey1 = (ECPublicKey) kp.getPublic();
        kp.genKeyPair();
        PrivateKey privateKey2 = kp.getPrivate();
        ECPublicKey publicKey2 = (ECPublicKey) kp.getPublic();
        // generate first secret
        KeyAgreement ka = KeyAgreement.getInstance(keyAgreementAlg, false);
        byte[] secret1 = new byte[128];
        byte[] public2 = new byte[128];
        short publicKeyLength = publicKey2.getW(public2, (short) 0);
        ka.init(privateKey1);
        ka.generateSecret(public2, (short) 0, publicKeyLength, secret1, (short) 0);
        // generate second secret
        byte[] secret2 = new byte[128];
        byte[] public1 = new byte[128];
        publicKeyLength = publicKey1.getW(public1, (short) 0);
        ka.init(privateKey2);
        ka.generateSecret(public1, (short) 0, publicKeyLength, secret2, (short) 0);
        assertEquals(true, Arrays.areEqual(secret1, secret2));
    }
}