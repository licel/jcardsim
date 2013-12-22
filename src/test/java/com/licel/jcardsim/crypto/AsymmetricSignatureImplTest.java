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
import javacard.security.PrivateKey;
import javacard.security.PublicKey;
import javacard.security.RSAPublicKey;
import javacard.security.RandomData;
import javacard.security.Signature;
import junit.framework.TestCase;
import org.bouncycastle.util.encoders.Hex;

/**
 * Test for
 * <code>AsymmetricSignatureImpl</code> Test data from NXP JCOP31-36 JavaCard
 */
public class AsymmetricSignatureImplTest extends TestCase {

    // rsa public key data from card
    final static String RSA_ETALON_MODULUS = "00D3038C1FCA3CB00A2B52D8EB9446B422F091FB0871715DB8747809461ADD98DDAE963F56B8CB21B00CE1E209D9BBF7FADE6580F8A5996EC9AB9455AF432D4994E261B0426A41DF155CEFF4CB464F9DCB9521AA9EEED2895E8B85D13469C0D5F22396314587D305740D2A219F7B641869DD8A995E5B928DC81DB385D140D48C71";
    final static String RSA_ETALON_EXP = "010001";
    final static short RSA_ETALON_KEY_SIZE = 1024;
    // etalon msg
    String MESSAGE = "C46A3D01F5494013F9DFF3C5392C64";
    // signatures from card
    final static String[] RSA_SIGNATURES = new String[]{
        // ALG_RSA_SHA_ISO9796
        "389629B307B396FB2BCE1379DD950A5D21B052169FB8789E3A5483FDBE85B5B9B1AC92C50FD8D38F829D5565024506D69FC0A3DAF99BE379F62BE7BAC14C64DA100301CE96A9202151F1F227CB9AC492573BDFED0209CA6DCB19099B907E8C54FFF8A6C7919F892242A720E6A9113D5C9DDADF2E6FA0903CD35A88B48ACC4E62",
        // ALG_RSA_SHA_PKCS1
        "A45ECBE4DC34A6EDDEFD547245720BBE706AE06EB1E162C04E556FAECD717C958C3134DDB99C4105E1DE2C38419A70B9C9094B192B1B68385B0A65399198C2685B6AC06F704E1EDAE638AD870599BC502801F4094D53354696DEB60C3453D718EB29D5815C395357DC6607EB9A6989DF7E2652899184D764BBA155B33AB6C267",
        // ALG_RSA_MD5_PKCS1
        "2BCBD3220787D8D621526FBF88E852158FB37B46A339EAAF64742548155C7A48DA3ED9D41B9A29BAB2582A81329411C9FB0FDE0BFCF962438440C68828C1FDC3B33A4F0FEE318FA9DA3802A5CBAA9E9D6EA3618A2626E9FDA6F5613335F868442E20B3EB2E5C0580CE6E999BBD33A9BD5C633FE18930916586D91527781F1081"
    };

    // RSA 2048-bit public key data from card
    final static String RSA_2048_MODULUS = "8D4E29049C1C1861557F78B399665AAF957B73EBCCF5A436ED204A3B2C88F7A73ACE693147A6A731FFC297FBDFEABBA3658FDA68F0C3A60C2D8A96F874293FADE00F6AF600091A59118E8388DC69FD2D737882FBBB624A1A8BBD89641CFE33FC9C85BBC99017FCFE5CD5D13A3FE70524BE870171B0DBC870CB2F123CA5540C3B0759B71871A08EE3521C66261DCC1BD8F95DC850B730D8B8F0CF80F19C71B35FCB0439F419E7BD8E7B6A55203EFC6191D79DEF46B8A8F47EDF38F9E618A885BDFB8B212919D41CA1AB2B9B48328A9F4CF552FC157B8A1361A5ACABEE27725E835622D633569291532384EE47817AEB75119294E8522444FCCCB4F54073C2E121";    
    // RSA 2048-bit Signature
    final static String RSA_2048_SIGNATURE = "27E3B9410C261BE66534B9FC8CB272902B8BFB06077CA973B39B512C48DD878FA4B00259E7B58ECD5F236127990DC6E10DAB1CAF4C967689C1D1A03A5C5EBD6EA617D5F0AEE09851207EC4BCE67287687F21AC450693B9B22CEC7EA87679FC6BCE1B2DA56F41BF9BD433932370379C103D269E9E529E46699E03E243B589DE5469B12E27B1F95E8FE6F872010A64126760AC51AEF44CDABE4D9FBC7568B12A077943E8CF3A4C3C1674B3600B1AA01CB2D290A0DC7968F1509BEB5ECC47C48789870D5279E675D67E262AF96CD8750D9C4CE450D64FDC8D135573B9BB0497B4A6F3F3034832707C116D2E3E51EC85D6BCF8AF681AC96245CD03D50CF5D391B0AF";

    public AsymmetricSignatureImplTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of verify method, of class AsymmetricSignatureImpl.
     */
    public void testVerifyRSA() {
        System.out.println("verify rsa");
        RSAPublicKey publicKey = (RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC,
                RSA_ETALON_KEY_SIZE, false);
        byte[] modulus = Hex.decode(RSA_ETALON_MODULUS);
        byte[] exponent = Hex.decode(RSA_ETALON_EXP);
        publicKey.setModulus(modulus, (short) 0, (short) modulus.length);
        publicKey.setExponent(exponent, (short) 0, (short) exponent.length);

        RSAPublicKey publicKey2048 = (RSAPublicKey) KeyBuilder.buildKey(KeyBuilder.TYPE_RSA_PUBLIC,
                KeyBuilder.LENGTH_RSA_2048, false);
        byte[] modulus2048 = Hex.decode(RSA_2048_MODULUS);
        publicKey2048.setModulus(modulus2048, (short) 0, (short) modulus2048.length);
        publicKey2048.setExponent(exponent, (short) 0, (short) exponent.length);
        
        // verify signs
        Signature signature = Signature.getInstance(Signature.ALG_RSA_SHA_ISO9796, false);
        testEngineVerify(signature, publicKey, Hex.decode(MESSAGE),
                Hex.decode(RSA_SIGNATURES[0]), (short) 0);

        signature = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1, false);
        testEngineVerify(signature, publicKey, Hex.decode(MESSAGE),
                Hex.decode(RSA_SIGNATURES[1]), (short) 0);

        signature = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1, false);
        testEngineVerify(signature, publicKey2048, new byte[24],
                Hex.decode(RSA_2048_SIGNATURE), (short) 0);
        
        signature = Signature.getInstance(Signature.ALG_RSA_MD5_PKCS1, false);
        testEngineVerify(signature, publicKey, Hex.decode(MESSAGE),
                Hex.decode(RSA_SIGNATURES[2]), (short) 0);

    }

    /**
     * SelfTest of RSA sign/verify method, of class AsymmetricSignatureImpl.
     */
    public void testSelfSignVerifyRSA() {
        System.out.println("selft test sign/verify rsa");
        testSelfSignVerify(KeyPair.ALG_RSA_CRT, RSA_ETALON_KEY_SIZE, Signature.ALG_RSA_SHA_PKCS1);
    }

   
    /**
     * SelfTest of ECDSA sign/verify method, of class AsymmetricSignatureImpl.
     */
    public void testSelfSignVerifyECDSA() {
        System.out.println("selft test sign/verify ecdsa");
        // ecf2m keys
        testSelfSignVerify(KeyPair.ALG_EC_F2M, KeyBuilder.LENGTH_EC_F2M_113, Signature.ALG_ECDSA_SHA);
        // ecfp keys
        testSelfSignVerify(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_112, Signature.ALG_ECDSA_SHA);
    }

    /**
     * Base SelfTest sign/verify method
     *
     * @param keyAlg - key generation algorithm
     * @param keySize - key size
     * @param signAlg - signature algorithm
     */
    public void testSelfSignVerify(byte keyAlg, short keySize, byte signAlg) {
        // generate keys
        KeyPair kp = new KeyPair(keyAlg, keySize);
        kp.genKeyPair();
        PrivateKey privateKey = kp.getPrivate();
        PublicKey publicKey = kp.getPublic();
        // init engine
        Signature signEngine = Signature.getInstance(signAlg, false);
        signEngine.init(privateKey, Signature.MODE_SIGN);
        // sign length + extra space
        byte[] signature = JCSystem.makeTransientByteArray((short) (128 + 10), JCSystem.CLEAR_ON_RESET);
        byte[] msg = JCSystem.makeTransientByteArray((short) 65, JCSystem.CLEAR_ON_RESET);
        RandomData rnd = RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);
        rnd.generateData(msg, (short) 0, (short) msg.length);
        short signLen = signEngine.sign(msg, (short) 0, (short) msg.length, signature, (short) 10);
        // issue https://code.google.com/p/jcardsim/issues/detail?id=14
        assertEquals(signLen<=signEngine.getLength(), true);
        Signature verifyEngine = Signature.getInstance(signAlg, false);
        testEngineVerify(verifyEngine, publicKey, msg, signature, (short) 10, signLen);
    }

     /**
     * Test the method
     * <code>verify</code> of
     * <code>Signature</code> engine
     *
     * @param engine tested engine
     * @param publicKey public key
     * @param etalonMsg etalon message bytes
     * @param etalonSign etalon signature bytes
     */
    public void testEngineVerify(Signature engine, PublicKey publicKey,
            byte[] etalonMsg, byte[] etalonSign, short etalonSignOffset) {
        testEngineVerify(engine, publicKey, etalonMsg, etalonSign, etalonSignOffset, (short) etalonSign.length);
    }   
    /**
     * Test the method
     * <code>verify</code> of
     * <code>Signature</code> engine
     *
     * @param engine tested engine
     * @param publicKey public key
     * @param etalonMsg etalon message bytes
     * @param etalonSign etalon signature bytes
     */
    public void testEngineVerify(Signature engine, PublicKey publicKey,
            byte[] etalonMsg, byte[] etalonSign, short etalonSignOffset, short etalonSignLength) {
        engine.init(publicKey, Signature.MODE_VERIFY);
        boolean result = engine.verify(etalonMsg, (short) 0, (short) etalonMsg.length,
                etalonSign, etalonSignOffset, (short)(etalonSignLength!=0?etalonSignLength:etalonSign.length));
        assertEquals(true, result);
    }
}
