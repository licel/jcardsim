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

import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.RandomData;
import javacard.security.RSAPublicKey;
import javacard.security.CryptoException;
import javacardx.crypto.Cipher;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;

import java.util.Random;

/**
 * Test for <code>AsymmetricCipherImpl</code>
 * Test data from NXP JCOP31-36 JavaCard
 */
public class AsymmetricCipherImplTest extends TestCase {

    public AsymmetricCipherImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * SelfTest of RSA Encryption/Decryption, of class AsymmetricCipherImpl.
     */
    public void testSelftRSA_NOPAD(){
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_512, (short) ((KeyBuilder.LENGTH_RSA_512/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_736, (short) ((KeyBuilder.LENGTH_RSA_736/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_768, (short) ((KeyBuilder.LENGTH_RSA_768/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_896, (short) ((KeyBuilder.LENGTH_RSA_896/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1024, (short) ((KeyBuilder.LENGTH_RSA_1024/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1280, (short) ((KeyBuilder.LENGTH_RSA_1280/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1536, (short) ((KeyBuilder.LENGTH_RSA_1536/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1984, (short) ((KeyBuilder.LENGTH_RSA_1984/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_2048, (short) ((KeyBuilder.LENGTH_RSA_2048/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_3072, (short) ((KeyBuilder.LENGTH_RSA_3072/Byte.SIZE) - 1));
        testSelftRSA(Cipher.ALG_RSA_NOPAD, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_4096, (short) ((KeyBuilder.LENGTH_RSA_4096/Byte.SIZE) - 1));
    }
    
    public void testSelftRSA_PKCS1(){
        // Refer to https://www.rfc-editor.org/rfc/rfc8017#section-7.2.1 and https://docs.oracle.com/javacard/3.0.5/api/javacardx/crypto/Cipher.html#ALG_RSA_PKCS1
        // mLen <= k - 11, k is the length in octets of the modulus n

        // Test at maximum message length, mLen = k - 11
        // RSA Key Pair
        short k = KeyBuilder.LENGTH_RSA_512/Byte.SIZE;
        short maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_512, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_736/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_736, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_768/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_768, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_896/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_896, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1024/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1024, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1280/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1280, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1536/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1536, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1984/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1984, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_2048/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_2048, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_3072/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_3072, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_4096/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_4096, maxMsgLen);

        // RSA Key Pair with private key in its Chinese Remainder Theorem form
        k = KeyBuilder.LENGTH_RSA_512/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_512, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_736/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_736, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_768/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_768, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_896/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_896, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1024/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1280/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1280, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1536/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1536, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1984/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1984,  maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_2048/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_2048, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_3072/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_3072, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_4096/Byte.SIZE;
        maxMsgLen = (short) (k - 11);
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_4096, maxMsgLen);

        // Test with mLen < k - 11
        testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_4096, (short) (maxMsgLen - 1));

        // Test with mLen > k - 11
        try{
            testSelftRSA(Cipher.ALG_RSA_PKCS1, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_4096, (short) (maxMsgLen + 1));
            assert(false);
        }
        catch (CryptoException ex) {
            assertEquals(CryptoException.ILLEGAL_USE, ex.getReason());
        }
    }

    public void testSelftRSA_PKCS1_OEAP(){
        // Refer to https://www.rfc-editor.org/rfc/rfc8017#section-7.1.1
        // mLen <= k - 2hLen - 2,
        //      k is the length in octets of the modulus n and
        //      hLen is the hash function output octet length, SHA1 is used as default https://www.rfc-editor.org/rfc/rfc8017#appendix-A.2.1
        short hLen = 20; // SHA1 hash size is 20 bytes

       // Test at maximum message length, mLen = k - 2hLen - 2
        // RSA Key Pair
        short k = KeyBuilder.LENGTH_RSA_512/Byte.SIZE;
        short maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_512, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_736/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_736, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_768/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_768, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_896/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_896, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1024/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1024, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1280/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1280, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1536/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1536, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1984/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_1984, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_2048/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_2048, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_3072/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_3072, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_4096/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA, KeyBuilder.LENGTH_RSA_4096, maxMsgLen);

        // RSA Key Pair with private key in its Chinese Remainder Theorem form
        k = KeyBuilder.LENGTH_RSA_512/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_512, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_736/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_736, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_768/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_768, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_896/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_896, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1024/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1280/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1280, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1536/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1536, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_1984/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1984, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_2048/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_2048, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_3072/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_3072, maxMsgLen);

        k = KeyBuilder.LENGTH_RSA_4096/Byte.SIZE;
        maxMsgLen = (short) (k - (2*hLen) - 2);
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_4096, maxMsgLen);

        // Test with mLen < k - 2hLen - 2
        testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_4096, (short) (maxMsgLen - 1));

        // Test with mLen > k - 2hLen - 2
        try{
            testSelftRSA(Cipher.ALG_RSA_PKCS1_OAEP, KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_4096, (short) (maxMsgLen + 1));
            assert(false);
        }
        catch (CryptoException ex) {
            assertEquals(CryptoException.ILLEGAL_USE, ex.getReason());
        }
    }

    private void testSelftRSA(byte algorithm, byte keyPairAlgorithm, short keySizeInBits, short messageLen) {
        Cipher cipher = Cipher.getInstance(algorithm, false);
        KeyPair kp = new KeyPair(keyPairAlgorithm, keySizeInBits);
        kp.genKeyPair();

        cipher.init(kp.getPublic(), Cipher.MODE_ENCRYPT);

        short keySizeInBytes = (short) (keySizeInBits/Byte.SIZE);
        byte[] msg = new byte[messageLen];
        byte[] encryptedMsg = new byte[keySizeInBytes];
        new Random().nextBytes(msg);

        cipher.doFinal(msg, (short) 0, (short) msg.length, encryptedMsg, (short) 0);

        cipher.init(kp.getPrivate(), Cipher.MODE_DECRYPT);
        byte[] decryptedMsg = new byte[msg.length];
        cipher.doFinal(encryptedMsg, (short) 0, (short) encryptedMsg.length, decryptedMsg, (short) 0);

        assertEquals(true, Arrays.areEqual(msg, decryptedMsg));
    }

    public void testRegression_CipherDoFinal_bufferPosNotReset() throws Exception {
        Cipher encryptEngine = Cipher.getInstance(Cipher.ALG_RSA_PKCS1, false);
        KeyPair keyPair = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024);
        keyPair.genKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        encryptEngine.init(publicKey, Cipher.MODE_ENCRYPT);

        byte[] buffer = new byte[256];
        encryptEngine.doFinal(buffer, (short) 0, (short) 59, buffer, (short) 0);
        try {
            encryptEngine.doFinal(buffer, (short) 0, (short) 59, buffer, (short) 0);
        }
        catch (CryptoException e) {
            // For RSA1024, data len into PKCS1 frame is 117B, but because AssymetricCipherImpl.bufferPos is not set
            // to 0 during doFinal(), it will emit exception because 68 + 68 > 117
            assert(false);
        }
    }
}
