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
import javacard.framework.Util;
import javacard.security.AESKey;
import javacard.security.KeyBuilder;
import junit.framework.TestCase;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.DESEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.util.Arrays;

/**
 * Test for <code>SymmetricKeyImpl</code>
 */
public class SymmetricKeyImplTest extends TestCase {

    public SymmetricKeyImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of clearKey method, of class SymmetricKeyImpl.
     */
    public void testClearKey() {
        System.out.println("clearKey");
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES);
        byte[] key = JCSystem.makeTransientByteArray((short) 8, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        desKey.setKey(key, (short) 0);
        desKey.clearKey();
        assertEquals(false, desKey.isInitialized());
    }

    /**
     * Test of setKey method, of class SymmetricKeyImpl.
     */
    public void testSetKey() {
        System.out.println("setKey");
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES);
        byte[] key = JCSystem.makeTransientByteArray((short) 8, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        desKey.setKey(key, (short) 0);
        assertEquals(true, desKey.isInitialized());
    }

    /**
     * Test of getKey method, of class SymmetricKeyImpl.
     */
    public void testGetKey() {
        System.out.println("getKey");
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES);
        byte[] key = JCSystem.makeTransientByteArray((short) 8, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        desKey.setKey(key, (short) 0);
        byte[] testKey = JCSystem.makeTransientByteArray((short) 8, JCSystem.CLEAR_ON_RESET);
        desKey.getKey(testKey, (short) 0);
        assertEquals(true, Arrays.areEqual(testKey, key));
    }

    /**
     * Test of getCipher method, of class SymmetricKeyImpl.
     */
    public void testGetCipher() {
        System.out.println("getCipher");
        // des key
        SymmetricKeyImpl desKey = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES);
        byte[] key = JCSystem.makeTransientByteArray((short) 8, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        desKey.setKey(key, (short) 0);
        boolean isValidCipher = (desKey.getCipher() instanceof DESEngine);
        assertEquals(true, isValidCipher);
        // 3des key
        SymmetricKeyImpl des3Key = new SymmetricKeyImpl(KeyBuilder.TYPE_DES, KeyBuilder.LENGTH_DES3_3KEY);
        key = JCSystem.makeTransientByteArray((short) 24, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        des3Key.setKey(key, (short) 0);
        isValidCipher = (des3Key.getCipher() instanceof DESedeEngine);
        assertEquals(true, isValidCipher);
        // aes key - 128
        AESKey aesKey = (AESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_128, false);
        key = JCSystem.makeTransientByteArray((short) 16, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        aesKey.setKey(key, (short) 0);
        // aes key - 192
        aesKey = (AESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_192, false);
        key = JCSystem.makeTransientByteArray((short) 24, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        aesKey.setKey(key, (short) 0);
        // aes key - 256
        aesKey = (AESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        key = JCSystem.makeTransientByteArray((short) 32, JCSystem.CLEAR_ON_RESET);
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        aesKey.setKey(key, (short) 0);
        // aes key - 256
        aesKey = (AESKey)KeyBuilder.buildKey(KeyBuilder.TYPE_AES, KeyBuilder.LENGTH_AES_256, false);
        key = new byte[32];
        Util.arrayFillNonAtomic(key, (short) 0, (short) key.length, (byte) 7);
        aesKey.setKey(key, (short) 0);

    }
}
