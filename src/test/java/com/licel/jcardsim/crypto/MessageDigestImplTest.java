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

import java.nio.ByteBuffer;
import java.security.SecureRandom;

import com.licel.jcardsim.utils.ByteUtil;
import javacard.framework.Util;
import javacard.security.InitializedMessageDigest;
import javacard.security.MessageDigest;
import junit.framework.TestCase;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
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
    // SHA-224 test vectors from NIST
    // https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Standards-and-Guidelines/documents/examples/SHA224.pdf
    static final String MESSAGE_24_NIST_SHA224 = "abc";
    static final String MD_24_NIST_SHA224 = "23097D22 3405D822 8642A477 BDA255B3 2AADBCE4 BDA0B3F7 E36C9DA7";
    static final String MESSAGE_448_NIST_SHA224 ="abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
    static final String MD_448_NIST_SHA224 = "75388B16 512776CC 5DBA5DA1 FD890150 B0C6455C B4F58B19 52522525 ";

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
    MessageDigestImpl engineSHA224 = new MessageDigestImpl(MessageDigest.ALG_SHA_224);
    MessageDigestImpl engineSHA256 = new MessageDigestImpl(MessageDigest.ALG_SHA_256);
    MessageDigestImpl engineSHA384 = new MessageDigestImpl(MessageDigest.ALG_SHA_384);
    MessageDigestImpl engineSHA512 = new MessageDigestImpl(MessageDigest.ALG_SHA_512);
    MessageDigestImpl engineSHA3_224 = new MessageDigestImpl(MessageDigest.ALG_SHA3_224);
    MessageDigestImpl engineSHA3_256 = new MessageDigestImpl(MessageDigest.ALG_SHA3_256);
    MessageDigestImpl engineSHA3_384 = new MessageDigestImpl(MessageDigest.ALG_SHA3_384);
    MessageDigestImpl engineSHA3_512 = new MessageDigestImpl(MessageDigest.ALG_SHA3_512);

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
        // sha224
        expResult = MessageDigest.ALG_SHA_224;
        result = engineSHA224.getAlgorithm();
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
        // sha3-224
        expResult = MessageDigest.ALG_SHA3_224;
        result = engineSHA3_224.getAlgorithm();
        assertEquals(expResult, result);
        // sha3-256
        expResult = MessageDigest.ALG_SHA3_256;
        result = engineSHA3_256.getAlgorithm();
        assertEquals(expResult, result);
        // sha3-384
        expResult = MessageDigest.ALG_SHA3_384;
        result = engineSHA3_384.getAlgorithm();
        assertEquals(expResult, result);
        // sha3-512
        expResult = MessageDigest.ALG_SHA3_512;
        result = engineSHA3_512.getAlgorithm();
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
        // sha224
        assertEquals(engineSHA224.getLength(), MessageDigest.LENGTH_SHA_224);
        // sha256
        assertEquals(engineSHA256.getLength(), MessageDigest.LENGTH_SHA_256);
        // sha384
        assertEquals(engineSHA384.getLength(), MessageDigest.LENGTH_SHA_384);
        // sha512
        assertEquals(engineSHA512.getLength(), MessageDigest.LENGTH_SHA_512);
        // sha3-244
        assertEquals(engineSHA3_224.getLength(), MessageDigest.LENGTH_SHA3_224);
        // sha3-256
        assertEquals(engineSHA3_256.getLength(), MessageDigest.LENGTH_SHA3_256);
        // sha3-384
        assertEquals(engineSHA3_384.getLength(), MessageDigest.LENGTH_SHA3_384);
        // sha3-512
        assertEquals(engineSHA3_512.getLength(), MessageDigest.LENGTH_SHA3_512);
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
     * Test SHA224 algorithm with test vectors from NIST
     */
    public void testSHA224() {
        System.out.println("test SHA224 doFinal()");
        testEngineDoFinal(engineSHA224, MESSAGE_24_NIST_SHA224.getBytes(), Hex.decode(MD_24_NIST_SHA224));
        System.out.println("test SHA224 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA224, MESSAGE_448_NIST_SHA224.getBytes(), Hex.decode(MD_448_NIST_SHA224));
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
     * Test SHA3-244 algorithm with NIST response file test vectors
     * https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/sha3/sha-3bytetestvectors.zip
     * File : SHA3_224ShortMsg.rsp
     */
    public void testSHA3_224() {
        // Empty message test vector
        String MESSAGE_0_NIST_SHA3_224 =  "";
        String MD_0_NIST_SHA3_224 = "6b4e03423667dbb73b6e15454f0eb1abd4597f9a1b078e3f5b5a6bc7";
        System.out.println("test SHA3-224 empty message doFinal()");
        testEngineDoFinal(engineSHA3_224, MESSAGE_0_NIST_SHA3_224.getBytes(), Hex.decode(MD_0_NIST_SHA3_224));

        String MESSAGE_448_NIST_SHA3_224 =  "6b2b92584146a433bee8b947cc1f35b617b73f5b1e0376ac8bdadfe5bfdf2263b205f74dfa53db7a29e5078f5c34a268119736ba390961f6";
        String MD_448_NIST_SHA3_224 = "132cfa7e71fe0991abbd88ef588ac95ac9289b1d775b42033567dd33";
        System.out.println("test SHA3-224 doFinal()");
        testEngineDoFinal(engineSHA3_224, Hex.decode(MESSAGE_448_NIST_SHA3_224), Hex.decode(MD_448_NIST_SHA3_224));

        String MESSAGE_896_NIST_SHA3_224 =  "d4f757d1c33b9c0b38b4e93e8e2483ec51b4861299f1d650961457496d86614d42a36e3696bf168fd4663efc26e88cd58d151e1531467b73f69dc9ce4f8d41ce579ce1c91e6760e340e7677abdf4fec1040745aa5144640a39b8c4f884df80753a691653003d634fa5bfce81f94ec3f6";
        String MD_896_NIST_SHA3_224 = "be11259377f09821d9dc358592b6565d8ef2b414dfaa7db5609fb751";

        System.out.println("test SHA3-244 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA3_224, Hex.decode(MESSAGE_896_NIST_SHA3_224), Hex.decode(MD_896_NIST_SHA3_224));
    }
    /**
     * Test SHA3-256 algorithm with NIST response file test vectors
     * https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/sha3/sha-3bytetestvectors.zip
     * File : SHA3_256ShortMsg.rsp
     */
    public void testSHA3_256() {
        // Empty message test vector
        String MESSAGE_0_NIST_SHA3_256 =  "";
        String MD_0_NIST_SHA3_256 = "a7ffc6f8bf1ed76651c14756a061d662f580ff4de43b49fa82d80a4b80f8434a";
        System.out.println("test SHA3-256 empty message doFinal()");
        testEngineDoFinal(engineSHA3_256, MESSAGE_0_NIST_SHA3_256.getBytes(), Hex.decode(MD_0_NIST_SHA3_256));

        String MESSAGE_448_NIST_SHA3_256 =  "00ff6c96b7aa3cf27d036cf20af7031434113252574bda9cf9244d85aef2593d3a7a83bff6be904b75164a1766828042bc3f4f090d98a03d";
        String MD_448_NIST_SHA3_256 = "d000eafca34815783bed9b050c6901c97f2e77d4771a0ed724dd8f6ff1448791";
        System.out.println("test SHA3-256 doFinal()");
        testEngineDoFinal(engineSHA3_256, Hex.decode(MESSAGE_448_NIST_SHA3_256), Hex.decode(MD_448_NIST_SHA3_256));

        String MESSAGE_896_NIST_SHA3_256 =  "8d93627c0b7cbf61a7fe70e78c2c8ed23b1344b4cfed31bd85980dd37b4690e5b8758f7d6d2269957a39a1ac3451cc196696ae9e9606a04089e13456095a1ce1e593481b3ac84f53f1cb10f789b099f316c948398ad52fa13474bdf486de9b431bd5d57ef9d83a42139a05f112b2bd08";
        String MD_896_NIST_SHA3_256 = "344ec86642eabb206b2fd930e4c5dde78aa878577d6c271cb0069d4999495652";

        System.out.println("test SHA3-256 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA3_256, Hex.decode(MESSAGE_896_NIST_SHA3_256), Hex.decode(MD_896_NIST_SHA3_256));
    }

    /**
     * Test SHA3-384 algorithm with NIST response file test vectors
     * https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/sha3/sha-3bytetestvectors.zip
     * File : SHA3_384ShortMsg.rsp
     */
    public void testSHA3_384() {
        // Empty message test vector
        String MESSAGE_0_NIST_SHA3_384 =  "";
        String MD_0_NIST_SHA3_384 = "0c63a75b845e4f7d01107d852e4c2485c51a50aaaa94fc61995e71bbee983a2ac3713831264adb47fb6bd1e058d5f004";
        System.out.println("test SHA3-384 empty message doFinal()");
        testEngineDoFinal(engineSHA3_384, MESSAGE_0_NIST_SHA3_384.getBytes(), Hex.decode(MD_0_NIST_SHA3_384));

        String MESSAGE_448_NIST_SHA3_384 =  "5415c2596aa7d21e855be98491bd702357c19f21f46294f98a8aa37b3532ee1541ca35509adbef9d83eb99528ba14ef0bd2998a718da861c3f16fe6971";
        String MD_448_NIST_SHA3_384 = "8f9fd7d879d6b51ee843e1fbcd40bb67449ae744db9f673e3452f028cb0189d9cb0fef7bdb5c760d63fea0e3ba3dd8d1";
        System.out.println("test SHA3-384 doFinal()");
        testEngineDoFinal(engineSHA3_384, Hex.decode(MESSAGE_448_NIST_SHA3_384), Hex.decode(MD_448_NIST_SHA3_384));

        String MESSAGE_768_NIST_SHA3_384 =  "00ce225eaea24843406fa42cc8450e66f76ac9f549b8591f7d40942f4833fc734a034c8741c551d57ddafb5d94ceb4b25680f045038306e6bcc53e88386e2b45b80b3ba23dec8c13f8ca01c202ae968c4d0df04cdb38395d2df42a5aff646928";
        String MD_768_NIST_SHA3_384 = "81d6e0d96575a9b8ca083ee9ec2ead57ddf72b97d7709086a2f4a749d3f61d16423463487562c7f09aba1b26e8cae47b";

        System.out.println("test SHA3-384 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA3_384, Hex.decode(MESSAGE_768_NIST_SHA3_384), Hex.decode(MD_768_NIST_SHA3_384));
    }
    /**
     * Test SHA3-512 algorithm with NIST response file test vectors
     * https://csrc.nist.gov/CSRC/media/Projects/Cryptographic-Algorithm-Validation-Program/documents/sha3/sha-3bytetestvectors.zip
     * File : SHA3_512ShortMsg.rsp
     */
    public void testSHA3_512() {
        // Empty message test vector
        String MESSAGE_0_NIST_SHA3_512 =  "";
        String MD_0_NIST_SHA3_512 = "a69f73cca23a9ac5c8b567dc185a756e97c982164fe25859e0d1dcc1475c80a615b2123af1f5f94c11e3e9402c3ac558f500199d95b6d3e301758586281dcd26";
        System.out.println("test SHA3-512 empty message doFinal()");
        testEngineDoFinal(engineSHA3_512, MESSAGE_0_NIST_SHA3_512.getBytes(), Hex.decode(MD_0_NIST_SHA3_512));

        String MESSAGE_448_NIST_SHA3_512 =  "302fa84fdaa82081b1192b847b81ddea10a9f05a0f04138fd1da84a39ba5e18e18bc3cea062e6df92ff1ace89b3c5f55043130108abf631e";
        String MD_448_NIST_SHA3_512 = "8c8eaae9a445643a37df34cfa6a7f09deccab2a222c421d2fc574bbc5641e504354391e81eb5130280b1226812556d474e951bb78dbdd9b77d19f647e2e7d7be";
        System.out.println("test SHA3-512 doFinal()");
        testEngineDoFinal(engineSHA3_512,Hex.decode(MESSAGE_448_NIST_SHA3_512), Hex.decode(MD_448_NIST_SHA3_512));

        String MESSAGE_576_NIST_SHA3_512 =  "0ce9f8c3a990c268f34efd9befdb0f7c4ef8466cfdb01171f8de70dc5fefa92acbe93d29e2ac1a5c2979129f1ab08c0e77de7924ddf68a209cdfa0adc62f85c18637d9c6b33f4ff8";
        String MD_576_NIST_SHA3_512 = "b018a20fcf831dde290e4fb18c56342efe138472cbe142da6b77eea4fce52588c04c808eb32912faa345245a850346faec46c3a16d39bd2e1ddb1816bc57d2da";

        System.out.println("test SHA3-512 doUpdate() + doFinal()");
        testEngineDoUpdateFinal(engineSHA3_512, Hex.decode(MESSAGE_576_NIST_SHA3_512), Hex.decode(MD_576_NIST_SHA3_512));
    }

    /**
     * Test method <code>doFinal</code>
     * @param engine tested engine
     * @param msg byte array contains etalon message
     * @param etalonDigest byte array contains etalon digest
     */
    public void testEngineDoFinal(MessageDigest engine, byte[] msg, byte[] etalonDigest) {
        byte[] digest = new byte[engine.getLength()];
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
        byte[] digest = new byte[engine.getLength()];
        engine.update(msg, (short) 0, (short) 7);
        engine.doFinal(msg, (short) 7, (short) (msg.length - 7), digest, (short) 0);
        assertEquals(true, Arrays.areEqual(etalonDigest, digest));
    }

    /**
     * Test of setInitialDigest method, of class MessageDigestImpl.
     */
    public void testSetInitialDigest() {
        byte[] initialDigestBuf = new byte[256];
        byte[] inputData = new byte[254];
        rnd.nextBytes(inputData);

        MessageDigestImpl[] digests = new MessageDigestImpl[]{engineSHA1, engineMD5, engineRIPEMD160,
                engineSHA224, engineSHA256, engineSHA384, engineSHA512,
                engineSHA3_224, engineSHA3_256,engineSHA3_384, engineSHA3_512};

        for (short i = 0; i < digests.length; i++) {
            System.out.println("testSetInitialDigest() - "+digests[i].getAlgorithm());

            byte[] digest = new byte[digests[i].getLength()];
            byte[] etalonDigest = new byte[digests[i].getLength()];
            digests[i].reset();
            digests[i].doFinal(inputData, (short) 0, (short) inputData.length, etalonDigest, (short) 0);

            // calc first part
            digests[i].reset();
            short part = digests[i].getBlockSize();
            digests[i].update(inputData, (short) 0, part);

            short initialDigestOff = (short) rnd.nextInt(initialDigestBuf.length - digests[i].getIntermediateStateSize());
            digests[i].getIntermediateDigest(initialDigestBuf, initialDigestOff);
            digests[i].reset();

            InitializedMessageDigest mdInstance = MessageDigest.getInitializedMessageDigestInstance(digests[i].getAlgorithm(),false);

            byte[] partBytes = new byte[2];
            partBytes[0] = (byte)((part >> 8) & 0xff);
            partBytes[1] = (byte)(part& 0xff);

            mdInstance.setInitialDigest(initialDigestBuf,initialDigestOff, (short) digests[i].getIntermediateStateSize(),partBytes, (short) 0, (short) partBytes.length);
            mdInstance.doFinal(inputData, part, (short) (inputData.length - part), digest, (short) 0);

            assertEquals(true, Arrays.areEqual(etalonDigest, digest));
        }

    }

}