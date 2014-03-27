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

import java.security.SecureRandom;
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
    // card response with algorithm SHA256
    static final String MD_1_NXP_SHA256 = "E700E0E6E5A4F3FF05CCBD4DA9CDBBEC712189DE65EF1ED19C351F7966270EF0";
    static final String MD_2_NXP_SHA256 = "17CB067C5E384B85DF370B96A5C91817D908F0C760CB2D7539EF8B9A7C02AB80";
    // NIST SHA-384 SHA ETALON MESSAGES
    static final String MESSAGE_64_NIST_SHA384 = "DE60275BDAFCE4B1";
    static final String MESSAGE_256_NIST_SHA384 = "BE01E520E69F04174CCF95455B1C81445298264D9ADC4958574A52843D95B8BA";
    // NIST SHA-384 ETALON DIGESTS
    static final String MD_64_NIST_SHA384 = "A3D861D866C1362423EB21C6BEC8E44B74CE993C55BAA2B6640567560EBECDAEDA07183DBBBD95E0F522CAEE5DDBDAF0";
    static final String MD_256_NIST_SHA384 = "C5CF54B8E3105B1C7BF7A43754D915B0947F28B6DC94A019182929B5C848E11441C9E4E90C7449F4C3CD12954F0F5D99";
    // NIST SHA-512 ETALON MESSAGES
    static final String MESSAGE_64_NIST_SHA512 = "6F8D58B7CAB1888C";
    static final String MESSAGE_256_NIST_SHA512 = "8CCB08D2A1A282AA8CC99902ECAF0F67A9F21CFFE28005CB27FCF129E963F99D";
    // NIST SHA-512 ETALON DIGESTS
    static final String MD_64_NIST_SHA512 = "A3941DEF2803C8DFC08F20C06BA7E9A332AE0C67E47AE57365C243EF40059B11BE22C91DA6A80C2CFF0742A8F4BCD941BDEE0B861EC872B215433CE8DCF3C031";
    static final String MD_256_NIST_SHA512 = "4551DEF2F9127386EEA8D4DAE1EA8D8E49B2ADD0509F27CCBCE7D9E950AC7DB01D5BCA579C271B9F2D806730D88F58252FD0C2587851C3AC8A0E72B4E1DC0DA6";
    
    
    MessageDigestImpl engineMD5 = new MessageDigestImpl(MessageDigest.ALG_MD5);
    MessageDigestImpl engineRIPEMD160 = new MessageDigestImpl(MessageDigest.ALG_RIPEMD160);
    MessageDigestImpl engineSHA1 = new MessageDigestImpl(MessageDigest.ALG_SHA);
    MessageDigestImpl engineSHA256 = new MessageDigestImpl(MessageDigest.ALG_SHA_256);
    MessageDigestImpl engineSHA384 = new MessageDigestImpl(MessageDigest.ALG_SHA_384);
    MessageDigestImpl engineSHA512 = new MessageDigestImpl(MessageDigest.ALG_SHA_512);
    
    SecureRandom rnd = new SecureRandom();
    
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
        // sha256
        expResult = MessageDigest.ALG_SHA_256;
        result = engineSHA256.getAlgorithm();
        assertEquals(expResult, result);
        // sha384
        expResult = MessageDigest.ALG_SHA_384;
        result = engineSHA384.getAlgorithm();
        assertEquals(expResult, result);
        // sha512
        expResult = MessageDigest.ALG_SHA_512;
        result = engineSHA512.getAlgorithm();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getLength method, of class MessageDigestImpl.
     */
    public void testGetLength() {
        System.out.println("getLength");
        // md5
        assertEquals(engineMD5.getLength(), MessageDigest.LENGTH_MD5);
        // ripemd160
        assertEquals(engineRIPEMD160.getLength(), MessageDigest.LENGTH_RIPEMD160);
        // sha1
        assertEquals(engineSHA1.getLength(), MessageDigest.LENGTH_SHA);
        // sha256
        assertEquals(engineSHA256.getLength(), MessageDigest.LENGTH_SHA_256);
        // sha384
        assertEquals(engineSHA384.getLength(), MessageDigest.LENGTH_SHA_384);
        // sha512
        assertEquals(engineSHA512.getLength(), MessageDigest.LENGTH_SHA_512);
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
     * Test SHA256 algorithm with card response
     */
    public void testSHA256() {
        System.out.println("test SHA256 doFinal()");
        testEngineDoFinal(engineSHA256, Hex.decode(MESSAGE1), Hex.decode(MD_1_NXP_SHA256));
        System.out.println("test SHA256 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA256, Hex.decode(MESSAGE2), Hex.decode(MD_2_NXP_SHA256));
    }
    
    /**
     * Test SHA384 algorithm with card response
     */
    public void testSHA384() {
        System.out.println("test SHA384 doFinal()");
        testEngineDoFinal(engineSHA384, Hex.decode(MESSAGE_64_NIST_SHA384), Hex.decode(MD_64_NIST_SHA384));
        System.out.println("test SHA384 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA384, Hex.decode(MESSAGE_256_NIST_SHA384), Hex.decode(MD_256_NIST_SHA384));
    }
    
    /**
     * Test SHA512 algorithm with card response
     */
    public void testSHA512() {
        System.out.println("test SHA512 doFinal()");
        testEngineDoFinal(engineSHA512, Hex.decode(MESSAGE_64_NIST_SHA512), Hex.decode(MD_64_NIST_SHA512));
        System.out.println("test SHA512 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA512, Hex.decode(MESSAGE_256_NIST_SHA512), Hex.decode(MD_256_NIST_SHA512));
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

    /**
     * Test of setInitialDigest method, of class MessageDigestImpl.
     */
    public void testSetInitialDigest() {
        byte[] initialDigestBuf = JCSystem.makeTransientByteArray((short) 128, JCSystem.CLEAR_ON_RESET);
        byte[] inputData = JCSystem.makeTransientByteArray((short) 254, JCSystem.CLEAR_ON_RESET);
        rnd.nextBytes(inputData);

        MessageDigestImpl[] digests = new MessageDigestImpl[]{engineSHA1, engineMD5, engineRIPEMD160,
            engineSHA256, engineSHA384, engineSHA512};

        for (short i = 0; i < digests.length; i++) {
            System.out.println("testSetInitialDigest() - "+digests[i].getAlgorithm());
            byte[] digest = JCSystem.makeTransientByteArray(digests[i].getLength(), JCSystem.CLEAR_ON_RESET);
            byte[] etalonDigest = JCSystem.makeTransientByteArray(digests[i].getLength(), JCSystem.CLEAR_ON_RESET);
            // calc first part
            short part = digests[i].getBlockSize();
            digests[i].update(inputData, (short) 0, part);
            short initialDigestOff = (short) rnd.nextInt(initialDigestBuf.length - digests[i].getLength());
            digests[i].getIntermediateDigest(initialDigestBuf, initialDigestOff);
            // doFinal
            digests[i].doFinal(inputData, part, (short) (inputData.length - part), digest, (short) 0);
            // etalon
            digests[i].reset();
            digests[i].update(inputData, (short) 0, (short) part);
            digests[i].doFinal(inputData, (short) part, (short) (inputData.length - part), etalonDigest, (short) 0);
            assertEquals(true, Arrays.areEqual(etalonDigest, digest));
        }
    }
    
    
}