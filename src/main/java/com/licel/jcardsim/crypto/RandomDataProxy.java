/*
 * Copyright 2015 Licel Corporation.
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

import javacard.security.CryptoException;
import javacard.security.RandomData;
import static javacard.security.RandomData.ALG_PSEUDO_RANDOM;
import static javacard.security.RandomData.ALG_SECURE_RANDOM;
import static javacard.security.RandomData.ALG_TRNG;
import static javacard.security.RandomData.ALG_FAST;
import static javacard.security.RandomData.ALG_KEYGENERATION;

/**
 * ProxyClass for <code>RandomData</code>
 * @see RandomData
 */
public class RandomDataProxy {
    /**
     * Creates a <code>RandomData</code> instance of the selected algorithm.
     * The pseudo random <code>RandomData</code> instance's seed is initialized to a internal default value.
     * @param algorithm the desired random number algorithm. Valid codes listed in ALG_ .. constants above. See <A HREF="../../javacard/security/RandomData.html#ALG_PSEUDO_RANDOM"><CODE>ALG_PSEUDO_RANDOM</CODE></A>.
     * @return the <code>RandomData</code> object instance of the requested algorithm
     * @throws CryptoException with the following reason codes:<ul>
     * <li><code>CryptoException.NO_SUCH_ALGORITHM</code> if the requested algorithm is not supported.</ul>
     */
    public static final RandomData getInstance(byte algorithm)
            throws CryptoException {
        RandomData instance = null;
        switch (algorithm) {
            case ALG_PSEUDO_RANDOM: 
            case ALG_SECURE_RANDOM:
            case ALG_TRNG:
            case ALG_FAST:
            case ALG_KEYGENERATION:
                instance = new RandomDataImpl(algorithm);
                break;
            default:
                CryptoException.throwIt((short) 3);
                break;
        }
        return instance;
    }
}
