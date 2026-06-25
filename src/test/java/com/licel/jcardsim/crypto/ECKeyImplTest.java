/*
 * Copyright 2013 Licel LLC.
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

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javacard.security.*;
import junit.framework.TestCase;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.math.ec.ECCurve;

/**
 * Test for <code>ECKeyImplTest</code>.
 */
public class ECKeyImplTest extends TestCase {
    
    public ECKeyImplTest(String testName) {
        super(testName);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    /**
     * Test of getKeyGenerationParameters method, of class ECKeyImpl.
     */
    public void testGetKeyGenerationParameters() {
        System.out.println("getKeyGenerationParameters");
        SecureRandom rnd = new SecureRandom();
        // public
        ECKeyImpl instance = new ECPublicKeyImpl(KeyBuilder.TYPE_EC_F2M_PUBLIC, KeyBuilder.LENGTH_EC_F2M_193);
        ECKeyGenerationParameters result = (ECKeyGenerationParameters) instance.getKeyGenerationParameters(rnd);
        assertEquals(result.getDomainParameters().getCurve()instanceof ECCurve.F2m, true);
        instance = new ECPublicKeyImpl(KeyBuilder.TYPE_EC_FP_PUBLIC, KeyBuilder.LENGTH_EC_FP_192);
        result = (ECKeyGenerationParameters) instance.getKeyGenerationParameters(rnd);
        assertEquals(result.getDomainParameters().getCurve()instanceof ECCurve.Fp, true);
        //private
        instance = new ECPrivateKeyImpl(KeyBuilder.TYPE_EC_F2M_PRIVATE, KeyBuilder.LENGTH_EC_F2M_193);
        result = (ECKeyGenerationParameters) instance.getKeyGenerationParameters(rnd);
        assertEquals(result.getDomainParameters().getCurve()instanceof ECCurve.F2m, true);
        instance = new ECPrivateKeyImpl(KeyBuilder.TYPE_EC_FP_PRIVATE, KeyBuilder.LENGTH_EC_FP_192);
        result = (ECKeyGenerationParameters) instance.getKeyGenerationParameters(rnd);
        assertEquals(result.getDomainParameters().getCurve()instanceof ECCurve.Fp, true);
    }

    public void testECKeyReuseLoop() throws Exception {
        byte[] message = "Hello Javacard ECDSA".getBytes(StandardCharsets.UTF_8);
        byte[] signature = new byte[100];

        // Instantiate KeyPair once to trigger reuse/recycling
        KeyPair ecKeyPair = new KeyPair(KeyPair.ALG_EC_FP, KeyBuilder.LENGTH_EC_FP_256);

        // Run iterations to trigger the 1/256 probability case
        for (int i = 0; i < 1000; i++) {
            ecKeyPair.genKeyPair();

            ECPrivateKey privKey = (ECPrivateKey) ecKeyPair.getPrivate();
            ECPublicKey pubKey = (ECPublicKey) ecKeyPair.getPublic();

            // Sign
            Signature signer = Signature.getInstance(Signature.ALG_ECDSA_SHA_256, false);
            signer.init(privKey, Signature.MODE_SIGN);
            short sigLen = signer.sign(message, (short) 0, (short) message.length, signature, (short) 0);

            // Verify
            Signature verifier = Signature.getInstance(Signature.ALG_ECDSA_SHA_256, false);
            verifier.init(pubKey, Signature.MODE_VERIFY);
            boolean verified = verifier.verify(message, (short) 0, (short) message.length, signature, (short) 0, sigLen);


            assertTrue("Verification failed at iteration " + i, verified);
        }
    }

}
