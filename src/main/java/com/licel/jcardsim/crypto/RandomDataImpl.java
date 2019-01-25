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

import java.util.concurrent.atomic.AtomicLong;

/**
 * Implementation <code>RandomData</code> based
 * on BouncyCastle CryptoAPI.
 * @see RandomData
 */
public class RandomDataImpl extends RandomData {
    byte algorithm;
    RandomGenerator engine;

    private static final long RANDOM_SEQUENCE_INITIAL = -31415926;      /* First 8 digits of Pi */
    private static final long RANDOM_SEQUENCE_INCREMENT = 10301;        /* First 5 digit palindromic prime */
    private static final AtomicLong sequence = new AtomicLong(RANDOM_SEQUENCE_INITIAL);

    public RandomDataImpl(byte algorithm) {
        this.algorithm = algorithm;
        this.engine = new DigestRandomGenerator(new SHA1Digest());

        // ALG_SECURE_RANDOM should not be consistent with each run
        if (ALG_SECURE_RANDOM == algorithm) {
            this.engine.addSeedMaterial(System.nanoTime());
            this.engine.addSeedMaterial(sequence.getAndAdd(RANDOM_SEQUENCE_INCREMENT));
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
