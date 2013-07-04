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
import javacard.security.KeyBuilder;
import javacard.security.KeyPair;
import javacard.security.RandomData;
import javacardx.crypto.Cipher;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;

/**
 * Test for <code>AsymmetricCipherImpl</code>
 * Test data from NXP JCOP31-36 JavaCard
 */
public class AssymetricCipherImplTest extends TestCase {

    public AssymetricCipherImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * SelfTest of RSA Encryption/Decryption, of class AssymetricCipherImpl.
     */
    public void testSelftRSA() {
        Cipher cipher = Cipher.getInstance(Cipher.ALG_RSA_NOPAD, false);
        KeyPair kp = new KeyPair(KeyPair.ALG_RSA_CRT, KeyBuilder.LENGTH_RSA_1024);
        kp.genKeyPair();

        cipher.init(kp.getPublic(), Cipher.MODE_ENCRYPT);
        byte[] msg = JCSystem.makeTransientByteArray((short) 127, JCSystem.CLEAR_ON_RESET);
        byte[] encryptedMsg = JCSystem.makeTransientByteArray((short) 128, JCSystem.CLEAR_ON_RESET);
        RandomData rnd = RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);
        rnd.generateData(msg, (short) 0, (short) msg.length);
        cipher.doFinal(msg, (short) 0, (short) msg.length, encryptedMsg, (short) 0);

        cipher.init(kp.getPrivate(), Cipher.MODE_DECRYPT);
        byte[] decryptedMsg = JCSystem.makeTransientByteArray((short) msg.length, JCSystem.CLEAR_ON_RESET);
        cipher.doFinal(encryptedMsg, (short) 0, (short) encryptedMsg.length, decryptedMsg, (short) 0);

        assertEquals(true, Arrays.areEqual(msg, decryptedMsg));

    }
}
