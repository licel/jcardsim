/*
 * Copyright 2022 Licel Corporation.
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

import javacard.security.*;
import javacardx.crypto.AEADCipher;
import javacardx.crypto.Cipher;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

import java.util.Random;

public class AuthenticatedSymmetricCipherImplTest extends TestCase {
    public AuthenticatedSymmetricCipherImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testAES_GCM_NotSupportKey(){
        byte[] desKey64bit = new byte[KeyBuilder.LENGTH_DES/Byte.SIZE];
        new Random().nextBytes(desKey64bit);

        // AEAD ciphers can be created by the Cipher.getInstance method using the ALG_AES_GCM and ALG_AES_CCM algorithm constants.
        // The returned Cipher instance should then be cast to AEADCipher.
        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);

        DESKey desKey = (DESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES, false);
        desKey.setKey(desKey64bit, (short) 0);

        try {
            engine.init(desKey, Cipher.MODE_ENCRYPT);
            fail("No exception");
        }
        catch (CryptoException e) {
            assertEquals(CryptoException.ILLEGAL_VALUE, e.getReason());
        }
    }



    // GCM sample test data downloaded from NIST.GOV
    // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/mac/gcmtestvectors.zip
    public void testAES_GCM_Sample_Key128Bit_AAD128Bit_Tag128Bit(){
        String[] testData = {
                "298efa1ccf29cf62ae6824bfc19557fc",                                 // 128-bit Key
                "6f58a93fe1d207fae4ed2f6d",                                         // 96-bit IV
                "cc38bccd6bc536ad919b1395f5d63801f99f8068d65ca5ac63872daf16b93901", // 256-bit PT  (Plain Text)
                "021fafd238463973ffe80256e5b1c6b1",                                 // 128-bit AAD (Additional Authenticated Data)
                "dfce4e9cd291103d7fe4e63351d9e79d3dfd391e3267104658212da96521b7db", // 256-bit CT (Cipher Text)
                "542465ef599316f73a7a560509a2d9f2"                                  // 128-bit Tag
        };

        testAES_GCM_PrearrangedResult(testData);
    }

    public void testAES_GCM_Sample_Key128Bit_NoAAD_Tag96Bit(){
        String[] testData = {
                "82ba8dc240bc3e5ea1c98ae5c8bc58a3",                                 // 128-bit Key
                "a016b0b2ab3e259f738ba228",                                         // 96-bit IV
                "42f6d57361d1afc1558ff23bd333b6adfa7fd622c436b27513c6391174a72473", // 256-bit PT  (Plain Text)
                "",
                "b7ea8f84d5b05f23d71678c4e546306d53703a25043cd7102579bac8cdd9bc4e", // 256-bit CT (Cipher Text)
                "796964243c22d258fa4fc4f4"                                          // 96-bit Tag
        };

        testAES_GCM_PrearrangedResult(testData);
    }

    public void testAES_GCM_Sample_Key128Bit_AAD128Bit_ShortTag64Bit(){
        String[] testData = {
                "76faaf2bfbd103b5fae725f4990b8282",                                 // 128-bit Key
                "4f32472c588fcbae5012ce70",                                         // 96-bit IV
                "58be7470b3b0de22a8f902fda1100215b56831805920be92a7e57d81c150acba", // 256-bit PT  (Plain Text)
                "6e4141d7f79d4e2682cd605e3e39033c",                                 // 128-bit AAD (Additional Authenticated Data)
                "11e3c43c549d277e42feb0d2ef39715ac8d86bd925ed7e64f17b97688daeef8b", // 256-bit CT (Cipher Text)
                "37d7c65a03635f8d"                                                  // 64-bit Tag
        };

        testAES_GCM_PrearrangedResult(testData);
    }

    public void testAES_GCM_Sample_Key192Bit_AAD128Bit_Tag112Bit(){
        String[] testData = {
                "8ef391e4b7a2fe05b959be27823357080f963ed2f64b9e59",    // 192-bit Key
                "0080052a2a5bb0e95222a419",                            // 96-bit IV
                "e7fb0631eebf9bdba87045b33650c4ce",                    // 128-bit PT  (Plain Text)
                "290322092d57479e20f6281e331d95a9",                    // 128-bit AAD (Additional Authenticated Data)
                "88d674044031414af7ba9da8b89dd68e",                    // 256-bit CT (Cipher Text)
                "69897d99d8e1706f38c613896c18"                         // 64-bit Tag
        };

        testAES_GCM_PrearrangedResult(testData);
    }

    public void testAES_GCM_Sample_Key192Bit_AAD160Bit_Tag128Bit() {
        String[] testData = {
                "95e5c8dcee4ef17571e1becc3f2d4ac8d5aa73e74b3f1115",    // 192-bit Key
                "e3b91649120f92b4f712644b",                            // 96-bit IV
                "eca3606b9e2a0c7a1c6c4b765176f643",                    // 128-bit PT  (Plain Text)
                "68b093733bd1e77448fe5687b74796834d1797cf",            // 160-bit AAD (Additional Authenticated Data)
                "0ff6d858cf0f5309c0f4b2747f6b551f",                    // 128-bit CT (Cipher Text)
                "94d6ac2796a9b9901933a0f9e5377979"                     // 128-bit Tag
        };

        testAES_GCM_PrearrangedResult(testData);
    }

    public void testAES_GCM_NotSupportTagLength() {

        byte[] key128Bit = new byte[128/Byte.SIZE];
        new Random().nextBytes(key128Bit);

        byte[] iv96Bit = new byte[96/Byte.SIZE];
        new Random().nextBytes(iv96Bit);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);

        short keyInBitSize = (short) (key128Bit.length * 8);
        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        aesKey.setKey(key128Bit, (short) 0);

        final short AAD_BYTES = 32;
        final short MSG_BYTES = 32;
        final short NOT_SUPPORT_TAG_LEN = 32;
        try {
            engine.init(aesKey, Cipher.MODE_ENCRYPT,iv96Bit,(short)0,(short)iv96Bit.length, AAD_BYTES, MSG_BYTES, NOT_SUPPORT_TAG_LEN);
            fail("No exception");
        }
        catch (CryptoException e) {
            assertEquals(CryptoException.ILLEGAL_VALUE, e.getReason());
        }

    }

    public void testAES_GCM_Key256Bit_AAD128Bit_Tag120Bit(){
        String[] testData = {
                // 256-bit Key
                "8bdb9073bca042d3bfe99240c438386c877d2a00b1f3bc9485aea034982b6779",

                // 96-bit IV
                "b2d1c505266a5b2eb32faa44",

                // 408-bit PT  (Plain Text)
                "1140acb00c1a37dffeead3f47b9c37b4140b7dd1965a8fbba76bcf7614b03398eb777f598bdd2599959a5b0ee6e1af75838888",

                // 160-bit AAD (Additional Authenticated Data)
                "182188be275f93fb909f61eba148fb62",

                // 408-bit CT  (Cipher Text)
                "1f99d4b40f9a9a5494d87215b447f2e7cbcaf6a141b12a9b2210ae9e8a99776b03346596adabc5872b7113d8099366a3e7bd36",

                // 120-bit Tag
                "3a4ca34a8b63e78a4405288a9b2738"
        };

        testAES_GCM_PrearrangedResult(testData);
    }

    public void testAES_GCM_PrearrangedResult(String[] testData){
        byte[] key = Hex.decode(testData[0]);
        byte[] iv = Hex.decode(testData[1]);
        byte[] plaintext = Hex.decode(testData[2]);
        byte[] aad = Hex.decode(testData[3]);
        byte[] ciphertext = Hex.decode(testData[4]);
        byte[] tag = Hex.decode(testData[5]);

        short keyInBitSize = (short) (key.length * 8);
        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES_TRANSIENT_RESET, keyInBitSize, false);
        aesKey.setKey(key, (short) 0);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);

        // Test encryption
        engine.init(aesKey, Cipher.MODE_ENCRYPT,iv,(short)0,(short)iv.length, (short) aad.length, (short) plaintext.length, (short) tag.length);
        engine.updateAAD(aad, (short) 0, (short) aad.length);

        byte[] encrypted = new byte[plaintext.length + tag.length];
        short encryptProcessedBytes = engine.doFinal(plaintext, (short) 0, (short) plaintext.length,encrypted, (short) 0);

        assertEquals(true,Arrays.areEqual(ciphertext,0,ciphertext.length,encrypted,0,ciphertext.length));
        assertEquals( encryptProcessedBytes, encrypted.length);

        byte[] retrievedTag = new byte[tag.length];
        engine.retrieveTag(retrievedTag, (short) 0, (short) retrievedTag.length);

        assertEquals(true, Arrays.areEqual(retrievedTag, tag));

        // Test decryption with wrong AAD
        byte[] wrongAAD = new byte[16];
        new Random().nextBytes(wrongAAD);

        engine.init(aesKey, Cipher.MODE_DECRYPT,iv,(short)0,(short)iv.length, (short) wrongAAD.length, (short) ciphertext.length, (short) tag.length);
        engine.updateAAD(wrongAAD, (short) 0, (short) wrongAAD.length);
        byte[] decrypted = new byte[ciphertext.length];

        short decryptProcessedBytes = 0;
        try {
            decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length),decrypted, (short) 0);
            fail("No exception");
        }
        catch (CryptoException e) {
            assertEquals(CryptoException.ILLEGAL_USE, e.getReason());
        }

        // Re-initiate to reset cipher
        engine.init(aesKey, Cipher.MODE_DECRYPT,iv,(short)0,(short)iv.length, (short) aad.length, (short) ciphertext.length, (short) tag.length);
        engine.updateAAD(aad, (short) 0, (short) aad.length);

        decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length),decrypted, (short) 0);
        assertEquals( decryptProcessedBytes, decrypted.length);

        assertEquals(true, Arrays.areEqual(decrypted, plaintext));
        assertEquals(true, engine.verifyTag(encrypted, (short) ciphertext.length, (short) tag.length, (short) tag.length));

    }

    public void testAES_GCM_SinglePartOfflineEncryptAndDecrypt(){
        byte[] key256Bit = new byte[256/Byte.SIZE];
        new Random().nextBytes(key256Bit);

        byte[] iv96Bit = new byte[96/Byte.SIZE];
        new Random().nextBytes(iv96Bit);

        byte[] aad128Bit = new byte[128/Byte.SIZE];
        new Random().nextBytes(aad128Bit);

        String msg =
                "Copyright 2022 Licel Corporation.\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n" +
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.\n";

        byte[] msgBytes = msg.getBytes();

        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        aesKey.setKey(key256Bit, (short) 0);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);

        final short tagLenInBits = 96;
        byte[] tag = new byte[tagLenInBits/Byte.SIZE];

        // Test encryption
        engine.init(aesKey, Cipher.MODE_ENCRYPT,iv96Bit,(short)0,(short)iv96Bit.length, (short) aad128Bit.length, (short) msgBytes.length, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] encrypted = new byte[msgBytes.length + tag.length];
        short encryptProcessedBytes = engine.doFinal(msgBytes, (short) 0, (short) msgBytes.length,encrypted, (short) 0);
        assertEquals( encryptProcessedBytes, encrypted.length);

        engine.retrieveTag(tag, (short) 0, (short) tag.length);

        // Decrypt back
        engine.init(aesKey, Cipher.MODE_DECRYPT,iv96Bit,(short)0,(short)iv96Bit.length, (short) aad128Bit.length, (short) encrypted.length, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] decrypted = new byte[msgBytes.length];
        short decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length),decrypted, (short) 0);

        assertEquals( decryptProcessedBytes, decrypted.length);
        assertEquals(true, Arrays.areEqual(decrypted, msgBytes));
        assertEquals(true, engine.verifyTag(tag, (short) 0, (short) tag.length, (short)(tagLenInBits/Byte.SIZE)));
    }

    public void testAES_GCM_MultiplePartOfflineEncryptAndDecrypt(){
        byte[] key256Bit = new byte[256/Byte.SIZE];
        new Random().nextBytes(key256Bit);

        byte[] iv96Bit = new byte[96/Byte.SIZE];
        new Random().nextBytes(iv96Bit);

        byte[] aad128Bit = new byte[128/Byte.SIZE];
        new Random().nextBytes(aad128Bit);

        String msgPart1 =
                "Copyright 2022 Licel Corporation.\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n";

        String msgPart2 =
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.\n";


        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        aesKey.setKey(key256Bit, (short) 0);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);

        final short tagLenInBits = 96;
        byte[] tag = new byte[tagLenInBits/Byte.SIZE];

        short totalMsgLen = (short)( msgPart1.length() + msgPart2.length());
        // Test encryption
        engine.init(aesKey, Cipher.MODE_ENCRYPT,iv96Bit,(short)0,(short)iv96Bit.length, (short) aad128Bit.length, totalMsgLen, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] encrypted = new byte[totalMsgLen + tag.length ];

        short encryptProcessedBytes = engine.update( msgPart1.getBytes(),(short) 0, (short) msgPart1.length(),encrypted,(short) 0);
        encryptProcessedBytes += engine.doFinal(msgPart2.getBytes(), (short) 0, (short) msgPart2.length(),encrypted, (short) encryptProcessedBytes);
        assertEquals( encryptProcessedBytes, encrypted.length);

        engine.retrieveTag(tag, (short) 0, (short) tag.length);

        // Decrypt back
        engine.init(aesKey, Cipher.MODE_DECRYPT,iv96Bit,(short)0,(short)iv96Bit.length, (short) aad128Bit.length, (short)encrypted.length, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] decrypted = new byte[totalMsgLen];
        short decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length),decrypted, (short) 0);

        assertEquals( decryptProcessedBytes, decrypted.length);
        assertEquals(true, Arrays.areEqual(decrypted, 0, msgPart1.length(), msgPart1.getBytes(), 0, msgPart1.length()));
        assertEquals(true, Arrays.areEqual(decrypted, msgPart1.length(), decrypted.length, msgPart2.getBytes(), 0, msgPart2.length()));
        assertEquals(true, engine.verifyTag(tag, (short) 0, (short) tag.length, (short)(tagLenInBits/Byte.SIZE)));
    }

    public void testAES_GCM_OnlineEncryptAndDecryptWithZeroIV(){
        byte[] key256Bit = new byte[256/Byte.SIZE];
        new Random().nextBytes(key256Bit);

        byte[] aad128Bit = new byte[128/Byte.SIZE];
        new Random().nextBytes(aad128Bit);

        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        aesKey.setKey(key256Bit, (short) 0);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);
        engine.init(aesKey,Cipher.MODE_ENCRYPT);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        String msgPart1 =
                "Copyright 2022 Licel Corporation.\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n";

        String msgPart2 =
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.\n";

        final short TAG_SIZE = 16;
        byte[] encrypted = new byte[msgPart1.length() + msgPart2.length() + TAG_SIZE];
        short encryptProcessedBytes = engine.update( msgPart1.getBytes(),(short) 0, (short) msgPart1.length(),encrypted,(short) 0);
        encryptProcessedBytes += engine.doFinal(msgPart2.getBytes(), (short) 0, (short) msgPart2.length(),encrypted, encryptProcessedBytes);
        assertEquals( encryptProcessedBytes, encrypted.length);

        byte[] tag = new byte[encryptProcessedBytes - (msgPart1.length() + msgPart2.length())];
        engine.retrieveTag(tag, (short) 0, (short) tag.length);

        // Decrypt back
        engine.init(aesKey, Cipher.MODE_DECRYPT);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] decrypted = new byte[msgPart1.length()+msgPart2.length()];
        short decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length),decrypted, (short) 0);

        assertEquals( decryptProcessedBytes, decrypted.length);
        assertEquals(true, Arrays.areEqual(decrypted, 0, msgPart1.length(), msgPart1.getBytes(), 0, msgPart1.length()));
        assertEquals(true, Arrays.areEqual(decrypted, msgPart1.length(), decrypted.length, msgPart2.getBytes(), 0, msgPart2.length()));
        assertEquals(true, engine.verifyTag(tag, (short) 0, (short) tag.length, TAG_SIZE));
    }

    public void testAES_GCM_OnlineEncryptAndDecryptWithIV(){
        byte[] key256Bit = new byte[256 / Byte.SIZE];
        new Random().nextBytes(key256Bit);

        byte[] aad128Bit = new byte[128 / Byte.SIZE];
        new Random().nextBytes(aad128Bit);

        byte[] iv96Bit = new byte[96 / Byte.SIZE];
        new Random().nextBytes(iv96Bit);

        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        aesKey.setKey(key256Bit, (short) 0);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);
        engine.init(aesKey, Cipher.MODE_ENCRYPT, iv96Bit, (short) 0, (short) iv96Bit.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        String msgPart1 =
                "Copyright 2022 Licel Corporation.\n" +
                "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                "you may not use this file except in compliance with the License.\n" +
                "You may obtain a copy of the License at\n" +
                "\n" +
                "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                "\n";

        String msgPart2 =
                "Unless required by applicable law or agreed to in writing, software\n" +
                "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                "See the License for the specific language governing permissions and\n" +
                "limitations under the License.\n";

        byte[] encrypted = new byte[msgPart1.length() + msgPart2.length() + aad128Bit.length];
        short encryptProcessedBytes = engine.update(msgPart1.getBytes(), (short) 0, (short) msgPart1.length(), encrypted, (short) 0);
        encryptProcessedBytes += engine.doFinal(msgPart2.getBytes(), (short) 0, (short) msgPart2.length(), encrypted, (short) encryptProcessedBytes);
        assertEquals(encryptProcessedBytes, encrypted.length);

        byte[] tag = new byte[encryptProcessedBytes - (msgPart1.length() + msgPart2.length())];
        engine.retrieveTag(tag, (short) 0, (short) tag.length);

        // Decrypt back
        engine.init(aesKey, Cipher.MODE_DECRYPT,iv96Bit, (short) 0, (short) iv96Bit.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] decrypted = new byte[msgPart1.length() + msgPart2.length()];
        short decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length), decrypted, (short) 0);

        assertEquals(decryptProcessedBytes, decrypted.length);
        assertEquals(true, Arrays.areEqual(decrypted, 0, msgPart1.length(), msgPart1.getBytes(), 0, msgPart1.length()));
        assertEquals(true, Arrays.areEqual(decrypted, msgPart1.length(), decrypted.length, msgPart2.getBytes(), 0, msgPart2.length()));
        assertEquals(true, engine.verifyTag(tag, (short) 0, (short) tag.length, (short) aad128Bit.length));
    }

    public void testAES_CCM_NotSupportOnlineEncryption(){
        byte[] key128bit = new byte[KeyBuilder.LENGTH_AES_128/Byte.SIZE];
        new Random().nextBytes(key128bit);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_CCM, false);

        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        aesKey.setKey(key128bit, (short) 0);

        try {
            // Call for online mode of encryption
            engine.init(aesKey, Cipher.MODE_ENCRYPT);
            fail("No exception");
        }
        catch (CryptoException e) {
            assertEquals(CryptoException.INVALID_INIT, e.getReason());
        }
    }

    // Use C.1 Example 1 in Appendix C of NIST Special Publication 800-38D
    // https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-38c.pdf
    // Supports only the 12 byte IV length, which is the value recommended by NIST Special Publication 800-38D 5.2.1.1 Input Data
    // https://docs.oracle.com/javacard/3.0.5/guide/supported-cryptography-classes.htm#JCUGC356
    public void testAES_CCM_Sample_Key128Bit_AAD64Bit_Tag32Bit_NotSupportNonce56Bit() {
        //In the following example, Klen = 128, Tlen=32, Nlen = 56, Alen = 64, and Plen = 32.
        String[] testData = {
                "40414243 44454647 48494a4b 4c4d4e4f",// Key
                "20212223",//Payload
                "00010203 04050607", //AAD
                "10111213 141516", //Nonce
                "7162015b 4dac255d", //Cipher text
                "6084341b" // 32-bit tag

        };

        byte[] key = Hex.decode(testData[0]);
        byte[] payload = Hex.decode(testData[1]);
        byte[] aad = Hex.decode(testData[2]);
        byte[] nonce = Hex.decode(testData[3]);
        byte[] ciphertext = Hex.decode(testData[4]);

        final short TAG_LEN = 4;

        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        aesKey.setKey(key, (short) 0);

        // AEAD ciphers can be created by the Cipher.getInstance method using the ALG_AES_GCM and ALG_AES_CCM algorithm constants.
        // The returned Cipher instance should then be cast to AEADCipher.
        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_CCM, false);
        try{
            engine.init(aesKey, Cipher.MODE_ENCRYPT, nonce, (short) 0, (short) nonce.length, (short) aad.length, (short) payload.length, TAG_LEN);
            fail("No exception");
        }
        catch (CryptoException e) {
            assertEquals(CryptoException.ILLEGAL_VALUE, e.getReason());
        }

    }

    // Use C.3 Example 3 in Appendix C of NIST Special Publication 800-38D
    // https://nvlpubs.nist.gov/nistpubs/Legacy/SP/nistspecialpublication800-38c.pdf
    public void testAES_CCM_Sample_Key128Bit_AAD160Bit_Tag64Bit() {
        //In the following example, Klen = 128, Tlen=64, Nlen = 96, Alen = 160, and Plen = 192.
        String[] testData = {
                "40414243 44454647 48494a4b 4c4d4e4f",// Key
                "20212223 24252627 28292a2b 2c2d2e2f 30313233 34353637",//Payload
                "00010203 04050607 08090a0b 0c0d0e0f 10111213", //AAD
                "10111213 14151617 18191a1b", //Nonce
                "e3b201a9 f5b71a7a 9b1ceaec cd97e70b 6176aad9 a4428aa5 484392fb c1b09951", //Cipher text
                "67c99240 c7d51048" // 64-bit tag

        };
        final short TAG_LEN = 8;
        testAES_CCM_PrearrangedResult(testData,TAG_LEN);
    }

    // Use sample vector from
    // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/mac/ccmtestvectors.zip
    public void testAES_CCM_Sample_Key128Bit_AAD128Bit_Tag128Bit() {
        String[] testData = {
                "005e8f4d8e0cbf4e1ceeb5d87a275848",// 128-bit Key
                "b6f345204526439daf84998f380dcfb4b4167c959c04ff65",// 24-byte Payload
                "2f1821aa57e5278ffd33c17d46615b77363149dbc98470413f6543a6b749f2ca", // 32-byte AAD
                "0ec3ac452b547b9062aac8fa", // 96-bit Nonce
                "9575e16f35da3c88a19c26a7b762044f4d7bbbafeff05d754829e2a7752fa3a14890972884b511d8", // 192-bit Cipher text
        };
        final short TAG_LEN = 16;
        testAES_CCM_PrearrangedResult(testData,TAG_LEN);
    }

    public void testAES_CCM_Sample_Key192Bit_AAD256Bit_Tag128Bit() {
        String[] testData = {
                "d49b255aed8be1c02eb6d8ae2bac6dcd7901f1f61df3bbf5",// 192-bit Key
                "fc375d984fa13af4a5a7516f3434365cd9473cd316e8964c",// Payload
                "4efbd225553b541c3f53cabe8a1ac03845b0e846c8616b3ea2cc7d50d344340c", // AAD
                "ca650ed993c4010c1b0bd1f2", // 96-bit Nonce
                "5b300c718d5a64f537f6cbb4d212d0f903b547ab4b21af56ef7662525021c5777c2d74ea239a4c44", //Cipher text
        };
        final short TAG_LEN = 16;
        testAES_CCM_PrearrangedResult(testData,TAG_LEN);
    }

    public void testAES_CCM_Sample_Key256Bit_AAD256Bit_Tag128Bit() {
        String[] testData = {
                "d6ff67379a2ead2ca87aa4f29536258f9fb9fc2e91b0ed18e7b9f5df332dd1dc",// 256-bit Key
                "98626ffc6c44f13c964e7fcb7d16e988990d6d063d012d33",// Payload
                "d50741d34c8564d92f396b97be782923ff3c855ea9757bde419f632c83997630", // AAD
                "2f1d0717a822e20c7cd28f0a", // 96-bit Nonce
                "50e22db70ac2bab6d6af7059c90d00fbf0fb52eee5eb650e08aca7dec636170f481dcb9fefb85c05", //Cipher text
        };
        final short TAG_LEN = 16;
        testAES_CCM_PrearrangedResult(testData,TAG_LEN);
    }
    private void testAES_CCM_PrearrangedResult(String[] testData, short tagLen){

        byte[] key = Hex.decode(testData[0]);
        byte[] payload = Hex.decode(testData[1]);
        byte[] aad = Hex.decode(testData[2]);
        byte[] nonce = Hex.decode(testData[3]);
        byte[] ciphertext = Hex.decode(testData[4]);

        boolean have_sample_tag = (testData.length == 6);
        byte[] sample_tag = new byte[tagLen];
        if( have_sample_tag ){
            sample_tag = Arrays.copyOf(Hex.decode(testData[5]),tagLen);
        }

        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, (short) (key.length*Byte.SIZE), false);
        aesKey.setKey(key, (short) 0);

        // AEAD ciphers can be created by the Cipher.getInstance method using the ALG_AES_GCM and ALG_AES_CCM algorithm constants.
        // The returned Cipher instance should then be cast to AEADCipher.
        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_CCM, false);
        engine.init(aesKey, Cipher.MODE_ENCRYPT, nonce, (short) 0, (short) nonce.length, (short) aad.length, (short) payload.length, tagLen);
        engine.updateAAD(aad, (short) 0, (short) aad.length);

        byte[] encrypted = new byte[payload.length + tagLen];
        short encryptProcessedBytes = engine.doFinal(payload, (short) 0, (short) payload.length,encrypted, (short) 0);

        assertEquals( encryptProcessedBytes, encrypted.length);
        assertEquals(true,Arrays.areEqual(ciphertext,encrypted));

        byte[] tag =new byte[tagLen];
        engine.retrieveTag(tag, (short) 0, (short) tag.length);

        if(have_sample_tag){
            assertEquals(true,Arrays.areEqual(tag,sample_tag));
        }

        // Test decryption
        engine.init(aesKey, Cipher.MODE_DECRYPT, nonce, (short) 0, (short) nonce.length, (short) aad.length, (short) encrypted.length, tagLen);
        engine.updateAAD(aad, (short) 0, (short) aad.length);

        byte[] decrypted = new byte[payload.length];
        short decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) encrypted.length, decrypted, (short) 0);

        assertEquals( decryptProcessedBytes, decrypted.length);
        assertEquals(true,Arrays.areEqual(payload,decrypted));

        // Verify tag
        assertEquals(true, engine.verifyTag(tag, (short) 0, (short) tag.length,tagLen));
    }

    public void testAES_CCM_SinglePartEncryptAndDecrypt(){
        byte[] key256Bit = new byte[256/Byte.SIZE];
        new Random().nextBytes(key256Bit);

        byte[] nonce96Bit = new byte[96/Byte.SIZE];
        new Random().nextBytes(nonce96Bit);

        byte[] aad128Bit = new byte[128/Byte.SIZE];
        new Random().nextBytes(aad128Bit);

        String msg =
                "Copyright 2022 Licel Corporation.\n" +
                        "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n" +
                        "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License.\n";

        byte[] msgBytes = msg.getBytes();

        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        aesKey.setKey(key256Bit, (short) 0);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_GCM, false);

        final short tagLenInBits = 96;
        byte[] tag = new byte[tagLenInBits/Byte.SIZE];

        // Test encryption
        engine.init(aesKey, Cipher.MODE_ENCRYPT,nonce96Bit,(short)0,(short)nonce96Bit.length, (short) aad128Bit.length, (short) msgBytes.length, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] encrypted = new byte[msgBytes.length + tag.length];
        short encryptProcessedBytes = engine.doFinal(msgBytes, (short) 0, (short) msgBytes.length,encrypted, (short) 0);
        assertEquals( encryptProcessedBytes, encrypted.length);

        engine.retrieveTag(tag, (short) 0, (short) tag.length);

        // Decrypt back
        engine.init(aesKey, Cipher.MODE_DECRYPT,nonce96Bit,(short)0,(short)nonce96Bit.length, (short) aad128Bit.length, (short) encrypted.length, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] decrypted = new byte[msgBytes.length];
        short decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length),decrypted, (short) 0);

        assertEquals( decryptProcessedBytes, decrypted.length);
        assertEquals(true, Arrays.areEqual(decrypted, msgBytes));
        assertEquals(true, engine.verifyTag(tag, (short) 0, (short) tag.length, (short)(tagLenInBits/Byte.SIZE)));
    }

    public void testAES_CCM_MultiplePartEncryptAndDecrypt(){
        byte[] key256Bit = new byte[256/Byte.SIZE];
        new Random().nextBytes(key256Bit);

        byte[] nonce96Bit = new byte[96/Byte.SIZE];
        new Random().nextBytes(nonce96Bit);

        byte[] aad128Bit = new byte[128/Byte.SIZE];
        new Random().nextBytes(aad128Bit);

        String msgPart1 =
                "Copyright 2022 Licel Corporation.\n" +
                        "Licensed under the Apache License, Version 2.0 (the \"License\");\n" +
                        "you may not use this file except in compliance with the License.\n" +
                        "You may obtain a copy of the License at\n" +
                        "\n" +
                        "      http://www.apache.org/licenses/LICENSE-2.0\n" +
                        "\n";

        String msgPart2 =
                "Unless required by applicable law or agreed to in writing, software\n" +
                        "distributed under the License is distributed on an \"AS IS\" BASIS,\n" +
                        "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n" +
                        "See the License for the specific language governing permissions and\n" +
                        "limitations under the License.\n";


        AESKey aesKey = (AESKey) KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        aesKey.setKey(key256Bit, (short) 0);

        AEADCipher engine = (AEADCipher) Cipher.getInstance(AEADCipher.ALG_AES_CCM, false);

        final short tagLenInBits = 96;
        byte[] tag = new byte[tagLenInBits/Byte.SIZE];

        short totalMsgLen = (short)( msgPart1.length() + msgPart2.length());
        // Test encryption
        engine.init(aesKey, Cipher.MODE_ENCRYPT,nonce96Bit,(short)0,(short)nonce96Bit.length, (short) aad128Bit.length, totalMsgLen, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] encrypted = new byte[totalMsgLen + tag.length ];

        short encryptProcessedBytes = engine.update( msgPart1.getBytes(),(short) 0, (short) msgPart1.length(),encrypted,(short) 0);
        encryptProcessedBytes += engine.doFinal(msgPart2.getBytes(), (short) 0, (short) msgPart2.length(),encrypted, (short) encryptProcessedBytes);
        assertEquals( encryptProcessedBytes, encrypted.length);

        engine.retrieveTag(tag, (short) 0, (short) tag.length);

        // Decrypt back
        engine.init(aesKey, Cipher.MODE_DECRYPT,nonce96Bit,(short)0,(short)nonce96Bit.length, (short) aad128Bit.length, (short)encrypted.length, (short) tag.length);
        engine.updateAAD(aad128Bit, (short) 0, (short) aad128Bit.length);

        byte[] decrypted = new byte[totalMsgLen];
        short decryptProcessedBytes = engine.doFinal(encrypted, (short) 0, (short) (encrypted.length),decrypted, (short) 0);

        assertEquals( decryptProcessedBytes, decrypted.length);
        assertEquals(true, Arrays.areEqual(decrypted, 0, msgPart1.length(), msgPart1.getBytes(), 0, msgPart1.length()));
        assertEquals(true, Arrays.areEqual(decrypted, msgPart1.length(), decrypted.length, msgPart2.getBytes(), 0, msgPart2.length()));
        assertEquals(true, engine.verifyTag(tag, (short) 0, (short) tag.length, (short)(tagLenInBits/Byte.SIZE)));
    }
}
