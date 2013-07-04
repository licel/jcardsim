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
 * Test for <code>AsymmetricSignatureImpl</code>
 * Test data from NXP JCOP31-36 JavaCard
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
        // verify signs
        Signature signature = Signature.getInstance(Signature.ALG_RSA_SHA_ISO9796, false);
        testEngineVerify(signature, publicKey, Hex.decode(MESSAGE),
                Hex.decode(RSA_SIGNATURES[0]));

        signature = Signature.getInstance(Signature.ALG_RSA_SHA_PKCS1, false);
        testEngineVerify(signature, publicKey, Hex.decode(MESSAGE),
                Hex.decode(RSA_SIGNATURES[1]));

        signature = Signature.getInstance(Signature.ALG_RSA_MD5_PKCS1, false);
        testEngineVerify(signature, publicKey, Hex.decode(MESSAGE),
                Hex.decode(RSA_SIGNATURES[2]));

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
        byte[] signature = JCSystem.makeTransientByteArray((short) 128, JCSystem.CLEAR_ON_RESET);
        byte[] msg = JCSystem.makeTransientByteArray((short) 65, JCSystem.CLEAR_ON_RESET);
        RandomData rnd = RandomData.getInstance(RandomData.ALG_PSEUDO_RANDOM);
        rnd.generateData(msg, (short) 0, (short) msg.length);
        signEngine.sign(msg, (short) 0, (short) msg.length, signature, (short) 0);
        Signature verifyEngine = Signature.getInstance(signAlg, false);
        testEngineVerify(verifyEngine, publicKey, msg, signature);
    }

    /**
     * Test the method <code>verify</code> of <code>Signature</code> engine
     * @param engine tested engine
     * @param publicKey public key
     * @param etalonMsg etalon message bytes
     * @param etalonSign etalon signature bytes
     */
    public void testEngineVerify(Signature engine, PublicKey publicKey,
            byte[] etalonMsg, byte[] etalonSign) {
        engine.init(publicKey, Signature.MODE_VERIFY);
        boolean result = engine.verify(etalonMsg, (short) 0, (short) etalonMsg.length,
                etalonSign, (short) 0, (short) etalonSign.length);
        assertEquals(true, result);
    }
}
