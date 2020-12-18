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

import javacard.framework.Util;
import javacard.security.CryptoException;
import javacard.security.RandomData;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.prng.DigestRandomGenerator;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.bouncycastle.util.encoders.Hex;

import java.security.SecureRandom;

/**
 * Implementation <code>RandomData</code> based
 * on BouncyCastle CryptoAPI.
 * @see RandomData
 */
public class RandomDataImpl extends RandomData {
    byte algorithm;
    RandomGenerator engine;

    public RandomDataImpl(byte algorithm) {
        this.algorithm = algorithm;
        this.engine = new DigestRandomGenerator(new SHA1Digest());

        final String randomSeed = System.getProperty("com.licel.jcardsim.randomdata.seed");
        final String doSecureRandom = System.getProperty("com.licel.jcardsim.randomdata.secure", "0");
        if (randomSeed != null){
            this.engine.addSeedMaterial(Hex.decode(randomSeed));
        }
        else if ("1".equals(doSecureRandom)){
            byte[] seed = new byte[32];
            SecureRandom randomGenerator = new SecureRandom();
            randomGenerator.nextBytes(seed);
            this.engine.addSeedMaterial(seed);
        }
    }

    public void generateData(byte[] buffer, short offset, short length) throws CryptoException {
        engine.nextBytes(buffer, offset, length);
    }

    public void setSeed(byte[] buffer, short offset, short length) {
        byte[] seed = new byte[length];
        Util.arrayCopyNonAtomic(buffer, offset, seed, (short) 0, length);
        engine.addSeedMaterial(seed);
    }

    public byte getAlgorithm() {
        return algorithm;
    }

    public short nextBytes(byte[] buffer, short offset, short length) throws CryptoException {
        engine.nextBytes(buffer, offset, length);
        return (short) (offset + length);
    }
}
