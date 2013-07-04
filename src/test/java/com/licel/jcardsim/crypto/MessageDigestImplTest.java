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
import javacard.security.MessageDigest;
import junit.framework.TestCase;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.encoders.Hex;

/**
 * Test for <code>MessageDigestImpl</code>
 * Test data from NXP JCOP31-36 JavaCard
 */
public class MessageDigestImplTest extends TestCase {

    static final String MESSAGE1 = "F9607F6E66B4162C";
    static final String MESSAGE2 = "26C69FC4C14399A399B5B28AD9CC5B91796BC2565C5580BAC1BE10808F71949D8B";
    // card response with algorithm MD5
    static final String MD_1_NXP_MD5 = "3A43CC845A3DF85404A97407877D3A77";
    static final String MD_2_NXP_MD5 = "5919172BA50F4944339260067E09387A";
    // card response with algorithm SHA1
    static final String MD_1_NXP_SHA1 = "94FF92DC796929290990BA74F4B125F04F9B510A";
    static final String MD_2_NXP_SHA1 = "01793FF98B954C3F60F276B179186C499F619A9D";
    MessageDigest engineMD5 = new MessageDigestImpl(MessageDigest.ALG_MD5);
    MessageDigest engineSHA1 = new MessageDigestImpl(MessageDigest.ALG_SHA);

    public MessageDigestImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getAlgorithm method, of class MessageDigestImpl.
     */
    public void testGetAlgorithm() {
        System.out.println("getAlgorithm");
        // md5
        byte expResult = MessageDigest.ALG_MD5;
        byte result = engineMD5.getAlgorithm();
        assertEquals(expResult, result);
        // sha1
        expResult = MessageDigest.ALG_SHA;
        result = engineSHA1.getAlgorithm();
        assertEquals(expResult, result);

    }

    /**
     * Test of getLength method, of class MessageDigestImpl.
     */
    public void testGetLength() {
        System.out.println("getLength");
        // md5
        byte expResult = 16;
        byte result = engineMD5.getLength();
        assertEquals(expResult, result);
        // sha1
        expResult = 20;
        result = engineSHA1.getLength();
        assertEquals(expResult, result);
    }

    /**
     * Test MD5 algorithm with card response
     */
    public void testMD5() {
        System.out.println("test MD5 doFinal()");
        testEngineDoFinal(engineMD5, Hex.decode(MESSAGE1), Hex.decode(MD_1_NXP_MD5));
        System.out.println("test MD5 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineMD5, Hex.decode(MESSAGE2), Hex.decode(MD_2_NXP_MD5));
    }

    /**
     * Test SHA1 algorithm with card response
     */
    public void testSHA() {
        System.out.println("test SHA1 doFinal()");
        testEngineDoFinal(engineSHA1, Hex.decode(MESSAGE1), Hex.decode(MD_1_NXP_SHA1));
        System.out.println("test SHA1 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA1, Hex.decode(MESSAGE2), Hex.decode(MD_2_NXP_SHA1));
    }

    /**
     * Test method <code>doFinal</code>
     * @param engine tested engine
     * @param msg byte array contains etalon message
     * @param etalonDigest byte array contains etalon digest
     */
    public void testEngineDoFinal(MessageDigest engine, byte[] msg, byte[] etalonDigest) {
        byte[] digest = JCSystem.makeTransientByteArray(engine.getLength(), JCSystem.CLEAR_ON_RESET);
        engine.doFinal(msg, (short) 0, (short) msg.length, digest, (short) 0);
        assertEquals(true, Arrays.areEqual(etalonDigest, digest));
    }

    /**
     * Test sequence method's calls <code>doUpdate();doFinal()</code>
     * @param engine tested engine
     * @param msg byte array contains etalon message
     * @param etalonDigest byte array contains etalon digest
     */
    public void testEngineDoUpdateFinal(MessageDigest engine, byte[] msg, byte[] etalonDigest) {
        byte[] digest = JCSystem.makeTransientByteArray(engine.getLength(), JCSystem.CLEAR_ON_RESET);
        engine.update(msg, (short) 0, (short) 7);
        engine.doFinal(msg, (short) 7, (short) (msg.length - 7), digest, (short) 0);
        assertEquals(true, Arrays.areEqual(etalonDigest, digest));
    }
}
