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

import javacard.security.DHPublicKey;
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
        testGenerateSecret(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, KeyAgreement.ALG_EC_SVDP_DH_PLAIN);
        testGenerateSecret(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, KeyAgreement.ALG_EC_SVDP_DH_PLAIN_XY);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_SVDP_DH);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_SVDP_DH_PLAIN);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_SVDP_DH_PLAIN_XY);
        System.out.println("test ecdhc");
        testGenerateSecret(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, KeyAgreement.ALG_EC_SVDP_DHC);
        testGenerateSecret(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, KeyAgreement.ALG_EC_SVDP_DHC_PLAIN);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_SVDP_DHC);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_SVDP_DHC_PLAIN);
        System.out.println("test ecgm");
        testGenerateSecret(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, KeyAgreement.ALG_EC_PACE_GM);
        testGenerateSecret(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, KeyAgreement.ALG_EC_PACE_GM);
    }
    
     /**
     * SelfTest of generateSecret method with DH algorithm, 
     * of class KeyAgreementImpl.
     */
    public void testGenerateSecretDH() {
        System.out.println("test dh");
        generateSecretDH(KeyPair.ALG_DH, KeyBuilder.LENGTH_DH_1024, KeyAgreement.ALG_DH_PLAIN);
        generateSecretDH(KeyPair.ALG_DH, KeyBuilder.LENGTH_DH_2048, KeyAgreement.ALG_DH_PLAIN);
    }

        /**
     * DH method generateSecret
     * @param keyAlg - key generation algorithm
     * @param keySize - key size
     * @param keyAgreementAlg - key agreement algorithm
     */
    public void generateSecretDH(byte keyAlg, short keySize, byte keyAgreementAlg) {
        // generate keys
        KeyPair kp = new KeyPair(keyAlg, keySize);
        KeyPair kp2 = new KeyPair(keyAlg, keySize);
        kp.genKeyPair();
        PrivateKey privateKey1 = kp.getPrivate();
        DHPublicKey publicKey1 = (DHPublicKey) kp.getPublic();
        kp2.genKeyPair();
        PrivateKey privateKey2 = kp2.getPrivate();
        DHPublicKey publicKey2 = (DHPublicKey) kp2.getPublic();
        // generate first secret
        KeyAgreement ka = KeyAgreement.getInstance(keyAgreementAlg, false);
        byte[] secret1 = new byte[256];
        byte[] public2 = new byte[256];
        short publicKeyLength = publicKey2.getY(public2, (short) 0);
        ka.init(privateKey1);
        short secret1Size = ka.generateSecret(public2, (short) 0, publicKeyLength, secret1, (short) 0);
        // generate second secret
        byte[] secret2 = new byte[256];
        byte[] public1 = new byte[256];
        publicKeyLength = publicKey1.getY(public1, (short) 0);
        ka.init(privateKey2);
        short secret2Size = ka.generateSecret(public1, (short) 0, publicKeyLength, secret2, (short) 0);
        
        // check match of values
        assertEquals(true, Arrays.areEqual(secret1, secret2));
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
        KeyPair kp2 = new KeyPair(keyAlg, keySize);
        kp.genKeyPair();
        PrivateKey privateKey1 = kp.getPrivate();
        ECPublicKey publicKey1 = (ECPublicKey) kp.getPublic();
        kp2.genKeyPair();
        PrivateKey privateKey2 = kp2.getPrivate();
        ECPublicKey publicKey2 = (ECPublicKey) kp2.getPublic();
        // generate first secret
        KeyAgreement ka = KeyAgreement.getInstance(keyAgreementAlg, false);
        byte[] secret1 = new byte[65];
        byte[] public2 = new byte[128];
        short publicKeyLength = publicKey2.getW(public2, (short) 0);
        ka.init(privateKey1);
        short secret1Size = ka.generateSecret(public2, (short) 0, publicKeyLength, secret1, (short) 0);
        // generate second secret
        byte[] secret2 = new byte[65];
        byte[] public1 = new byte[128];
        publicKeyLength = publicKey1.getW(public1, (short) 0);
        ka.init(privateKey2);
        short secret2Size = ka.generateSecret(public1, (short) 0, publicKeyLength, secret2, (short) 0);
        
        // check expected length
        switch (keyAgreementAlg) {
            case KeyAgreement.ALG_EC_SVDP_DH: // no break
            case KeyAgreement.ALG_EC_SVDP_DHC: 
                // sha1 size = 20
                assertEquals(secret1Size, 20);
                assertEquals(secret2Size, 20);
                break;
            case KeyAgreement.ALG_EC_SVDP_DHC_PLAIN: // no break
            case KeyAgreement.ALG_EC_SVDP_DH_PLAIN:
                // round up bit size of key to whole bytes
                assertEquals(secret1Size, (int) Math.ceil(keySize / 8.0));
                assertEquals(secret2Size, (int) Math.ceil(keySize / 8.0));
                break;
            case KeyAgreement.ALG_EC_SVDP_DH_PLAIN_XY: // no break
            case KeyAgreement.ALG_EC_PACE_GM:
                int fieldSize = (int) Math.ceil(keySize / 8.0);
                assertEquals(secret1Size, 1 + fieldSize + fieldSize);
                assertEquals(secret2Size, 1 + fieldSize + fieldSize);
                break;
            default:
                assertTrue(false); // unsupported algorithm
        }

        // check match of values
        assertEquals(true, Arrays.areEqual(secret1, secret2));
    }
}
